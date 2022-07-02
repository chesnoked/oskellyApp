package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.cart.ProductCartability;
import su.reddot.domain.service.comment.CommentService;
import su.reddot.domain.service.comment.CommentView;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.like.LikeService;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.domain.service.subscription.SubscriptionService;
import su.reddot.domain.service.user.UserService;
import su.reddot.domain.service.wishlist.WishListService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.mobile.api.v1.response.ProductResponse;
import su.reddot.presentation.mobile.api.v1.response.Size;
import su.reddot.presentation.view.product.ProductView;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/product")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor()
public class ProductRestControllerV1 {

	private final ProductService      productService;
	private final ProductItemService  productItemService;
	private final ImageService        imageService;
	private final CommentService      commentService;
	private final UserService         userService;
	private final ProfileService      profileService;
	private final LikeService         likeService;
	private final FollowingService    followingService;
	private final WishListService     wishListService;
	private final SubscriptionService subscriptionService;

	private CartService cartService;

	@Autowired
	public void setCartService(@Qualifier("shoppingCartService") CartService cs) {
		cartService = cs;
	}


	@GetMapping(value = "/{productId}")
	public ProductResponse getProduct(@PathVariable(value = "productId") Long productId, UserIdAuthenticationToken t) {
		DetailedProduct d = productService.getProduct(productId).orElseThrow(IllegalArgumentException::new);
		Product p = d.getProduct();
		d.setSizes(productItemService.getAvailableItemsSummary(p));

		SellerRequisite sellerRequisite = p.getSeller().getSellerRequisite();
		ProfileView sellerProfile = profileService.getProfileView(p.getSeller().getId());
		User me = userService.getUserById(t.getUserId()).orElseThrow(IllegalAccessError::new);
		ProductView productView = productService.getProductView(productId, me).orElseThrow(IllegalArgumentException::new);
		Size size = new Size(
				p.getSizeType().getAbbreviation(),
				d.getSizes().stream()
						.map(s -> new Size.Value(
								s.getSize().getId(),
								s.getSize().getBySizeType(p.getSizeType()),
								s.getLowestPrice())
						)
						.collect(Collectors.toList())
		);
		List<ProductResponse.Attribute> attributes = d.getAttributeValues()
				.stream()
				.map(a -> new ProductResponse.Attribute(a.getAttribute().getName(), a.getValue()))
				.collect(Collectors.toList());
		attributes.add(new ProductResponse.Attribute("ID товара", productId.toString()));

		ProductCartability cartability
				= cartService.checkProductCartability(p, me);

		return new ProductResponse(
				p.getId(),
				p.getSeller().getId(),
				p.getSeller().getNickname(),
				p.getSeller().getRegistrationTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
				sellerRequisite != null? sellerRequisite.getCity() : null,
				p.getBrand().getId(),
				p.getBrand().getName(),
				p.getCategory().getDisplayName(),
				p.getCategory().getId(),
				size,
				p.getRrpPrice(),
				p.getProductCondition().getName(),
				p.getDescription(),
				attributes,
				p.isOurChoice(),
				imageService.getProductImages(p).stream().map(ProductImage::getSmallImageUrl).collect(Collectors.toList()),
				imageService.getProductImages(p).stream().map(ProductImage::getLargeImageUrl).collect(Collectors.toList()),
				sellerProfile.getAvatarUrl(),
				p.getSeller().isPro(),
				sellerProfile.getCountSoldProducts(),
				sellerProfile.getPublishedProductsCount(),
				likeService.countByLikeable(p),
				followingService.isFollowingExists(me, p.getSeller()),
				wishListService.hasProduct(p, me),
				subscriptionService.isPriceSubscriptionExist(me, p),
				p.isAvailable(),
				likeService.doesUserLike(p, me),
				productView.getOfferRelated(),
				Utils.prettyRoundToCents(size.getValues().stream().map(Size.Value::getLowestPrice).findFirst().orElseGet(null)),
				productView.getStartPrice(),
				productView.getCurrentPrice(),
				productView.isHasDiscount(),
				cartability.isProductIsCartable(),
				cartability.getNonCartabilityReason()
		);
	}

	@DeleteMapping(value = "/{productId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long productId, UserIdAuthenticationToken token) {
		Product product = productService.getProduct(productId).orElseThrow(() -> new IllegalArgumentException("Товар не найден")).getProduct();
		if (product.isDeleted()) {
			throw new IllegalArgumentException("Товар уже удален!");
		}
		if(!token.getUserId().equals(product.getSeller().getId())) {
			throw new IllegalArgumentException("Товар вам не принадлежит");
		}
		productService.delete(product);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{productId}/comments")
	public ResponseEntity<?> getComments(@PathVariable Long productId, TimeZone timezone) {
		List<CommentView> comments = commentService.getComments(productId, timezone);
		return ResponseEntity.ok(comments);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{productId}/comment")
	public ResponseEntity<?> publishComment(
			@PathVariable Long productId,
			@RequestParam String text,
			UserIdAuthenticationToken token,
			TimeZone timezone
	) {
		CommentView comment = commentService.publishComment(text, token.getUserId(), productId, timezone);
		List<CommentView> comments = Collections.singletonList(comment);
		return ResponseEntity.ok(comments);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/like/toggle")
	public ResponseEntity<Void> likeToggle(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalAccessError::new);
		likeService.toggle(detailedProduct.getProduct(), user);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/like")
	public boolean like(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalAccessError::new);
		return likeService.like(detailedProduct.getProduct(), user);
	}

	@PostMapping("/{id}/pricesubscription")
	public boolean subscribeOnPrice(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new);
		return subscriptionService.subscribeOnPrice(user, detailedProduct.getProduct());
	}

	@PostMapping("/{id}/priceFollowing/toggle")
	public ResponseEntity<Boolean> subscribeOnPriceToggle(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(subscriptionService.toggle(user, detailedProduct.getProduct()));
	}

	@DeleteMapping("/{id}/pricesubscription")
	public boolean unsubscribeOnPrice(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new);
		return subscriptionService.unsubscribeOnPrice(user, detailedProduct.getProduct());
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/dislike")
	public boolean dislike(
			@PathVariable Long id,
			UserIdAuthenticationToken token
	) {
		DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalAccessError::new);
		return likeService.dislike(detailedProduct.getProduct(), user);
	}
}
