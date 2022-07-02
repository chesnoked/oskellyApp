package su.reddot.presentation.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.item.view.ProductItemToSell;
import su.reddot.domain.service.product.view.OfferView;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.profile.ProfileSellerRequest;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.domain.service.profile.view.ProductSubscription;
import su.reddot.domain.service.profile.view.SoldProduct;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.validation.ProfileImageValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 18.06.17.
 */
@Controller
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;
	private final UserService userService;
	private final ProfileImageValidator profileImageValidator;

	private final ProductItemService productItemService;
	private final ProductService     productService;
	private final FollowingService   followingService;

	private final NotificationService notificationService;

	@GetMapping
	public String getAccount() {
		return "profile/account";
	}

	@GetMapping("/products")
	public String getProductPage(
			Model m,
			UserIdAuthenticationToken token,
			@RequestParam(value = "state", required = false) String productStateString) {

		ProductState productState = null;
		try {
			productState = productStateString != null?
					ProductState.valueOf(productStateString)
					: null;
		}
		catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
		}

		ProductsList productsList = profileService.getCatalogProductPage(token.getUserId(), productState,
				/* Продавец сам просматривает свою страницу */
				token.getUserId());

		m.addAttribute("productsList", productsList);
		m.addAttribute("productStates", ProductState.values());
		m.addAttribute("currentProductState", productState);
		m.addAttribute("currentUserId", token.getUserId());

		return "profile/products";
	}

	@GetMapping("/products/items/{id}/sale-confirmation")
	public String saleConfirmationPage(@PathVariable Long id, UserIdAuthenticationToken t, Model m)
			throws ProductNotFoundException {

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		Optional<ProductItemToSell> itemToSaleIfAny = productItemService.getForSaleConfirmation(id, user);

		if (itemToSaleIfAny.isPresent()) {
			m.addAttribute("itemToSell", itemToSaleIfAny.get());
		}
		else {
			m.addAttribute("errorMessage", "Товар не найден");
			return "oops";
		}

		return "profile/sale-confirmation";
	}

	@GetMapping("/orders")
	public String getOrdersPage(Model m, UserIdAuthenticationToken t) {

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		List<OrderView> orders = profileService.getOrders(user);
		m.addAttribute("orders", orders);

		return "profile/orders";
	}

	@GetMapping("/offers/{id}")
    public String confirmOffer(@PathVariable long id, UserIdAuthenticationToken t, Model m) {

		User seller = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		Optional<OfferView> productOffer = productService.getProductOffer(id, seller);
		if (productOffer.isPresent()) {
			m.addAttribute("offer", productOffer.get());
			return "profile/offer";
		}
		else {
			m.addAttribute("errorMessage", "Предложение о снижении цены не найдено");
			return "oops";
		}
	}

	@PutMapping(value = "/info")
	public ResponseEntity<?> updatePersonalInfo(ProfileService.PersonalInfo personalInfo, UserIdAuthenticationToken t) {

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		profileService.updatePersonalInfo(personalInfo, user);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/address")
	public ResponseEntity<?> saveAddressInfo(ProfileService.Delivery delivery, UserIdAuthenticationToken t) {

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		profileService.updateDeliveryRequisite(delivery, user);

		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/image")
	public ResponseEntity<?> uploadImage(MultipartFile image, UserIdAuthenticationToken token) {
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "image");
		profileImageValidator.validate(image, bindingResult);
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}
		profileService.updateAvatar(image, token.getUserId());
		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/seller")
	public ResponseEntity<?> saveSellerInfo(@ModelAttribute ProfileSellerRequest request, UserIdAuthenticationToken token) {
		profileService.updateSellerInfo(request, token.getUserId());
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/seller")
	public String getSellerPage() {
		return "profile/seller";
	}

	@GetMapping("/wishlist")
	public String getMyWishlist(Model model, UserIdAuthenticationToken token){
		model.addAttribute("productCards", profileService.getMyWishList(token.getUserId()));
		return "profile/mywishlist";
	}

	@GetMapping("/followers")
	public String followers(Model m, UserIdAuthenticationToken t){

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		m.addAttribute("followers",
				followingService.getFor(user)
						.followers()
						.withFollowAvailability(user)
						.build());

		return "profile/followers";
	}

	@GetMapping("/followees")
	public String followees(Model m, UserIdAuthenticationToken t) {

		User user = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		m.addAttribute("followees",
				followingService.getFor(user)
						.followees()
						.withFollowAvailability(user)
						.build());

		return "profile/followees";
	}

	@GetMapping("/favorites")
	public String getMyFavorites(Model model, UserIdAuthenticationToken token){

		model.addAttribute("productCards", profileService.getMyFavorites(token.getUserId()));

		return "profile/my-liked-products";
	}

	@GetMapping("/notifications")
	public String getMyNotification(Model model, UserIdAuthenticationToken token){
		User user = userService.getUserById(token.getUserId())
				.orElseThrow(IllegalArgumentException::new);

		model.addAttribute("myNotifications", notificationService.findNotificationsByUserAndPresentAsView(user.getId(), null));

		notificationService.readAll(user);
		model.addAttribute("notifications", 0);

		return "profile/my-notifications";
	}

	@GetMapping("/pricesubscriptions")
	public String getMyPriceSubscriptions(Model model, UserIdAuthenticationToken token){
		model.addAttribute("productCards", token != null? profileService.getMyPriceSubscriptions(token.getUserId()) : null);
		return "profile/price-alterations/my-price-subscriptions";
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers")
	public String offers(Model m, UserIdAuthenticationToken t){

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
	    m.addAttribute("offers", profileService.getOffersFrom(u));

		return "profile/price-alterations/offers";
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers-to-me")
	public String offersToMe(Model m, UserIdAuthenticationToken t){

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		m.addAttribute("offers", profileService.getOffersTo(u));

		return "profile/price-alterations/offers-to-me";
	}

	@ModelAttribute
	public ProfileView getProfileView(UserIdAuthenticationToken token) {
		return token != null? profileService.getProfileView(token.getUserId()) : null;
	}

	/** На страницу профиля могут попасть только аутентифицированные пользователи. */
	@ModelAttribute
	public User authenticatedUser(UserIdAuthenticationToken t) {

		return userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
	}

	@GetMapping("/news")
	public String getNewsView(Model model, UserIdAuthenticationToken token) {

		model.addAttribute("actions", notificationService.findNewsByUserAndPresentAsView(token.getUserId(), true, null));

		return "profile/profile-news";
	}

	@GetMapping("/sales")
	public String sales(@RequestParam(required = false) SalesStrategy type, Model m, UserIdAuthenticationToken t) {

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);

		SalesStrategy actualType = type == null? SalesStrategy.newly : type;
        actualType.getProductsAndPopulateModel(m, u, profileService);

        m.addAttribute("types", SalesStrategy.values());
        m.addAttribute("currentType", actualType);

		return "profile/profile-sales";
	}

	@GetMapping("/subscriptions/products")
	public String subscriptions(Model m, UserIdAuthenticationToken t) {

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalArgumentException::new);

		List<ProductSubscription> productSubscriptions = profileService.getProductSubscriptions(u);
		if (!productSubscriptions.isEmpty()) {
			m.addAttribute("productSubscriptions", productSubscriptions);
		}

		return "profile/profile-report-availability";
	}

	/** Различные типы продаж. */
	@Getter @RequiredArgsConstructor
	public enum SalesStrategy {

	    /** Продажи, которые требуют подтверждения. */
		newly("Новые продажи") {
			@Override
			void getProductsAndPopulateModel(Model m, User u, ProfileService service) {
				List<SoldProduct> productsToConfirm = service.getProductsToConfirm(u);
				m.addAttribute("products", productsToConfirm);
			}
		},

		/**
		 *  Товары с подтвержденной продавцом продажей,
		 *  по которым не получен конечный статус от логистов об успешной доставке покупателю.
		 */
		pending("Текущие продажи") {
			@Override
			void getProductsAndPopulateModel(Model m, User u, ProfileService service) {
				List<SoldProduct> salePendingProducts = service.getSalePendingProducts(u);
				m.addAttribute("products", salePendingProducts);
			}
		},

		sold("Закрытые продажи") {
			@Override
			void getProductsAndPopulateModel(Model m, User u, ProfileService service) {
				List<SoldProduct> soldProducts = service.getSoldProducts(u);
				m.addAttribute("products", soldProducts);
			}
		};

		/** Понятное человеку название типа продаж. */
		private final String humanFriendly;

		abstract void getProductsAndPopulateModel(Model m, User u, ProfileService service);
	}
}
