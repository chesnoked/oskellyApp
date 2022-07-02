package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.api.ProductApiService;
import su.reddot.domain.service.product.api.ProductRequest;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.publication.info.PublicationInfoService;
import su.reddot.domain.service.publication.info.view.ProductConditionView;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.mobile.api.v1.response.Attribute;
import su.reddot.presentation.mobile.api.v1.response.CatalogProductPageResponse;
import su.reddot.presentation.mobile.api.v1.response.Product;
import su.reddot.presentation.mobile.api.v1.response.Size;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 12.08.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/catalog")
@Slf4j
@RequiredArgsConstructor
public class CatalogRestControllerV1 {

	private static final int PUBLISHED_ITEMS_SIZE = 15;

    private final ProductApiService      productApiService;
    private final CategoryService        categoryService;
    private final ProductService         productService;
    private final BrandService           brandService;
    private final PublicationInfoService publicationInfoService;
	private final ProfileService 		 profileService;
	private final UserService			 userService;

	@GetMapping("/products")
	public CatalogProductPageResponse getCategory(ProductRequest request, UserIdAuthenticationToken t) {
		CatalogProductPage page = productApiService.getCatalogProductPage(request, userService.getUserById(t.getUserId()).orElse(null));
		return of(page, false);
	}

	@GetMapping("/search")
	public ResponseEntity<?> search(@RequestParam String search) {
		//FIXME захардкодил limit
		List<ProductCard> cards = productService.findProducts(search,500);
		return ResponseEntity.ok(of(cards));
	}

	@GetMapping
	public List<CatalogCategory> getTree() {
		return categoryService.getEntireCatalog();
	}

	@GetMapping(value = "/brands")
	public ResponseEntity<List<Brand>> getBrands(@RequestParam(required = false) Long categoryId) {
		List<Long> interestedCategories;
		if(categoryId == null) {
			interestedCategories = Collections.emptyList();
		}
		else {
			interestedCategories = categoryService.getLeafCategoriesIds(categoryId);
		}
		return ResponseEntity.ok(productService.getActualBrands(interestedCategories));
	}

	@GetMapping(value = "/groupedBrands")
	public ResponseEntity<Map<String, List<Brand>>> getGroupedBrands() {
		List<Brand> brands = brandService.getAll();
		Map<String, List<Brand>> groups = brands.stream().collect(Collectors.groupingBy(b -> b.getName().substring(0,1)));
		return ResponseEntity.ok(groups);
	}

	@GetMapping(value = "/new")
	public CatalogProductPageResponse getNew(UserIdAuthenticationToken t) {

		ProductService.FilterSpecification spec = new ProductService.FilterSpecification()
				.state(ProductState.PUBLISHED)
				.itemState(ProductItem.State.INITIAL);

        ProductService.ViewQualification viewSettings = new ProductService.ViewQualification()
                .pageLength(PUBLISHED_ITEMS_SIZE)
				.interestingUser(userService.getUserById(t.getUserId()).orElse(null))
				.withSavings(true);

		CatalogProductPage page = productService.getProducts(
				spec, 1, ProductService.SortAttribute.PUBLISH_TIME_DESC, viewSettings);

		return of(page, true);
	}

	@GetMapping(value = "/new2")
	public CatalogProductPageResponse getNew2() {

		ProductService.FilterSpecification spec = new ProductService.FilterSpecification()
				.state(ProductState.PUBLISHED)
				.itemState(ProductItem.State.INITIAL);

		ProductService.ViewQualification viewSettings = new ProductService.ViewQualification()
				.pageLength(PUBLISHED_ITEMS_SIZE).withSavings(true);

		CatalogProductPage page = productService.getProducts(
				spec, 1, ProductService.SortAttribute.PUBLISH_TIME_DESC, viewSettings);

		return of(page, true);
	}

	@GetMapping(value = "/attributes/{categoryId}")
	public ResponseEntity<List<Attribute>> getAttributes(@PathVariable Long categoryId) {
		List<Attribute> attributes = categoryService.getAllAttributes(categoryId).stream()
				.map(a -> new Attribute(
						a.getAttribute().getId(),
						a.getAttribute().getName(),
						a.getValues().stream().map(v -> new su.reddot.presentation.mobile.api.v1.response.AttributeValue(v.getId(), a.getAttribute().getId(), a.getAttribute().getName(), v.getValue())).collect(Collectors.toList())))
				.collect(Collectors.toList());
		return ResponseEntity.ok(attributes);
	}

	@GetMapping(value = "/conditions/{productId}")
	public ResponseEntity<List<ProductConditionView>> getConditions(@PathVariable Long productId) {
        su.reddot.domain.model.product.Product p = productService.getProduct(productId).orElseThrow(IllegalArgumentException::new).getProduct();
		List<ProductConditionView> productConditions = publicationInfoService.getProductConditions(p);
		return ResponseEntity.ok(productConditions);
	}

	@GetMapping(value = "/conditions")
	public ResponseEntity<List<ProductConditionView>> getConditions() {
		List<ProductConditionView> productConditions = publicationInfoService.getProductConditions(null);
		return ResponseEntity.ok(productConditions);
	}

	@GetMapping(value = "/sizes/{categoryId}")
	public ResponseEntity<List<CatalogSize>> getSizes(@PathVariable Long categoryId) {
		List<CatalogSize> sizes = categoryService.getSizesGroupedBySizeType(categoryId);
		return ResponseEntity.ok(sizes);
	}

	@GetMapping("/wishlist/{profileId}")
	public ResponseEntity<CatalogProductPageResponse> getMyWishlist(@PathVariable Long profileId){
		List<ProductCard> wishList = profileService.getMyWishList(profileId);
		return ResponseEntity.ok(of(wishList));
	}

	@GetMapping("/favorites/{profileId}")
	public ResponseEntity<CatalogProductPageResponse> getMyFavorites(@PathVariable Long profileId){
		List<ProductCard> favorites = profileService.getMyFavorites(profileId);
		return ResponseEntity.ok(of(favorites));
	}

	@GetMapping("/pricefollowings")
	public ResponseEntity<CatalogProductPageResponse> getMyPriceSubscriptions(UserIdAuthenticationToken token){
		return ResponseEntity.ok(of(profileService.getMyPriceSubscriptions(token.getUserId())));
	}

	/**
	 * Передалать на of(List<ProductCard> cards)
	 * @see CatalogRestControllerV1#of(List)
	 */
	@Deprecated
	private CatalogProductPageResponse of(CatalogProductPage page, boolean lastWeek) {
		List<Product> products = page.getProducts().stream()
				.map(product -> new Product(
						product.getProduct().getId(),
                        Utils.prettyRoundToTens(product.getLowestPrice()),
						product.getProduct().getCategory().getDisplayName(),
						product.getProduct().getBrand().getName(),
						product.getPrimaryImage().getThumbnailUrl(),
						product.getSizeSummary() != null ?
								new Size(
									product.getSizeSummary().getAbbreviation(),
									product.getSizeSummary().getValues().stream().map(s -> new Size.Value(null, s, null)).collect(Collectors.toList())
								) :
								null,
						product.getLikesCount(),
						product.isLiked(),
						product.getProduct().getProductCondition().getSortOrder().equals(1L),
						Optional.ofNullable(product.getLowestPrice()).map(lp -> lp.compareTo(product.getStartPriceForLowestPrice()) == -1).orElse(false),
						product.getRrp() != null? Utils.formatPrice(product.getRrp()) : null,
						product.getSavingsValue()
				))
				.collect(Collectors.toList());
		CatalogProductPageResponse response = new CatalogProductPageResponse();
		response.setTotalItemsCount(lastWeek ? productService.getLastWeekPublishedProductsCount() : page.getProductsTotalAmount());
		response.setTotalPagesCount(page.getTotalPages());
		response.setItems(products);
		return response;
	}

	private CatalogProductPageResponse of(List<ProductCard> cards) {
		List<Product> products = cards.stream()
				.map(c -> new Product(
						c.getId(),
						Utils.prettyRoundToTens(c.getLowestPrice()),
						c.getCategory(),
						c.getBrand(),
						c.getPrimaryImageUrl(),
						c.getSizeSummary() != null ?
								new Size(
									 	c.getSizeSummary().getAbbreviation(),
										c.getSizeSummary().getValues().stream().map(s -> new Size.Value(null, s, null)).collect(Collectors.toList())
								) :
								null,
						c.getLikesCount(),
						c.isLiked(),
						c.isNotUsedYet(),
						Optional.ofNullable(c.getLowestPrice()).map(lp -> lp.compareTo(c.getStartPriceForLowestPrice()) == -1).orElse(false),
						c.getRrp() != null? Utils.formatPrice(c.getRrp()) : null,
						c.getSavingsValue()
				))
				.collect(Collectors.toList());

		CatalogProductPageResponse response = new CatalogProductPageResponse();
		response.setItems(products);
		return response;
	}
}
