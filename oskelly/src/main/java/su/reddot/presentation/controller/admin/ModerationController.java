package su.reddot.presentation.controller.admin;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.enums.AuthorityName;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.admin.moderation.ModerationService;
import su.reddot.domain.service.admin.moderation.ProductSizeMappingRequest;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.propublication.ProPublicationService;
import su.reddot.domain.service.propublication.exception.ProPublicationException;
import su.reddot.domain.service.propublication.view.ProPublicationGridInfo;
import su.reddot.domain.service.propublication.view.ProPublicationRequest;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.domain.service.publication.info.PublicationInfoService;
import su.reddot.domain.service.publication.info.view.ProductConditionView;
import su.reddot.domain.service.publication.info.view.PublicationInfoView;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.infrastructure.util.ErrorNotification;
import su.reddot.presentation.Utils;
import su.reddot.presentation.validation.ProductSizeMappingRequestValidator;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static su.reddot.presentation.Utils.badResponseWithFieldError;

@Controller
@RequestMapping("/admin/moderation")
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
@Slf4j
public class ModerationController {

	private final BrandService                       brandService;
	private final ModerationService                  moderationService;
	private final PublicationInfoService             publicationInfoService;
	private final PublicationService                 publicationService;
	private final ProductSizeMappingRequestValidator productSizeMappingRequestValidator;
	private final UserService                        userService;
	private final ProductService                     productService;
	private final ProPublicationService              proPublicationService;
	private final CategoryService                    categoryService;

	/**
	 * Доступ в этот контроллер может получить либо модератор
	 * либо обычный пользователь со статусом PRO
	 *
	 * @param token
	 */
	@ModelAttribute
	public void checkAuthorities(UserIdAuthenticationToken token) {
		Optional<User> user = userService.getUserById(token.getUserId());
		if (!user.isPresent()) {
			throw new AccessDeniedException("Access denied");
		} else if (!user.get().isPro() && !token.hasAuthority(AuthorityName.PRODUCT_MODERATION)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	/**
	 * Проверка доступа к конкретному товару
	 * Обычный прошник имеет доступ только к своему товару,
	 * модератор к любому
	 *
	 * @param productId
	 * @param token
	 */
	@ModelAttribute("/{productId}")
	public void checkAuthoritiesForPathProduct(
			@PathVariable(required = false) Long productId,
			UserIdAuthenticationToken token
	) {
		if (productId != null) {
			checkAuthoritiesForProduct(productId, token);
		}
	}

	/**
	 * @param productId
	 * @param token
	 * @see ModerationController#checkAuthoritiesForPathProduct(Long, UserIdAuthenticationToken)
	 */
	@ModelAttribute
	public void checkAuthoritiesForParamProduct(
			@RequestParam(required = false) Long productId,
			UserIdAuthenticationToken token
	) {
		if (productId != null) {
			checkAuthoritiesForProduct(productId, token);
		}
	}

	private void checkAuthoritiesForProduct(Long productId, UserIdAuthenticationToken token) {
		Optional<User> user = userService.getUserById(token.getUserId());
		if (!user.isPresent()) {
			throw new AccessDeniedException("Access denied");
		}
		Optional<DetailedProduct> product = productService.getProduct(productId);
		if (
				product.isPresent() &&
						!token.hasAuthority(AuthorityName.PRODUCT_MODERATION) &&
						!user.get().getId().equals(product.get().getProduct().getSeller().getId())
				) {
			throw new AccessDeniedException("Access denied");
		}
	}

	@GetMapping("")
	public String getProducts(
			Model m,
			Optional<Integer> page,
			UserIdAuthenticationToken token,
			@RequestParam(value = "addMore", defaultValue = "false") Boolean addMore
	) {
		int validPage = page.orElse(1);
		CatalogProductPage catalogProductPage;
		if (token.hasAuthority(AuthorityName.PRODUCT_MODERATION)) {
			catalogProductPage = moderationService.getCatalogProductPage(validPage); // если ты модератор, то видишь все товары
		} else {
			catalogProductPage = moderationService.getCatalogProductPage(validPage, token.getUserId()); // если ты обычный прошник, то видишь только свои товары
		}
		m.addAttribute("pageWithProducts", catalogProductPage);
		m.addAttribute("productStates", ProductState.values());
		m.addAttribute("activeTab", "products moderation");
		m.addAttribute("isModerator", token.hasAuthority(AuthorityName.PRODUCT_MODERATION));
		m.addAttribute("brands", brandService.getAll());
		m.addAttribute("rootCategories", categoryService.getEntireCatalog());
		m.addAttribute("addMore", addMore);
		List<ProductConditionView> productConditions = publicationInfoService.getProductConditions(null);
		productConditions.add(0, new ProductConditionView().setId(null).setName("Все").setChecked(true));
		m.addAttribute("productConditions", productConditions);
		List<CatalogCategory> leafCategories = categoryService.getLeafCategories(null).stream()
				.map(c -> {
					List<CatalogCategory> parentCategories = categoryService.getAllParentCategories(c.getId());
					String path = parentCategories.stream().map(CatalogCategory::getDisplayName).collect(Collectors.joining(" -> "));
					return new CatalogCategory(c.getId(), c.getDisplayName() + " (" + path + ")", c.isHasChildren());
				})
				.collect(Collectors.toList());
		leafCategories.sort(Comparator.comparing(CatalogCategory::getDisplayName));
		m.addAttribute("leafs", leafCategories);
		return token.hasAuthority(AuthorityName.PRODUCT_MODERATION) ? "admin/moderation/product_list" : "profile/pro-list";
	}

	@GetMapping("/{productId}")
	public String getProduct(
			Model m,
			@PathVariable Long productId,
			HttpServletResponse response,
			UserIdAuthenticationToken token
	) {
		m.addAttribute("activeTab", "products moderation");
		Optional<DetailedProduct> product = moderationService.getDetailedProduct(productId);
		if (!product.isPresent()) {
			response.setStatus(404);
			return token.hasAuthority(AuthorityName.PRODUCT_MODERATION) ? "admin/moderation/product_not_found" : "profile/product_not_found";
		}
		//TODO: использовать только либо DetailedProduct лмбо PublicationInfoView. Лучше PublicationInfoView
		m.addAttribute("product", product.get());
		PublicationInfoView infoView = publicationInfoService.getPublicationInfoForProduct(product.get());
		m.addAttribute("publicationInfo", infoView);
		List<Brand> brands = brandService.getAll();
		if (!brands.isEmpty()) {
			m.addAttribute("brands", brands);
		}
		m.addAttribute("canEdit", canUserEditProduct(token, product.get().getProduct()));
		m.addAttribute("isModerator", token.hasAuthority(AuthorityName.PRODUCT_MODERATION));
		Optional<User> user = userService.getUserById(token.getUserId());
		m.addAttribute("sellerIsPro", product.get().getProduct().getSeller().isPro());
		return token.hasAuthority(AuthorityName.PRODUCT_MODERATION) ? "admin/moderation/product_page" : "profile/pro-product";
	}

	@PutMapping("/attributes")
	public ResponseEntity<?> updateAttributesAndCategory(
			@RequestParam Long productId,
			@RequestParam Long category,
			@RequestParam(value = "attributeValues[]", required = false) List<Long> attributeValues,
			UserIdAuthenticationToken token
	) {
		checkAccessBeforeEdit(token, productId);
		Optional<DetailedProduct> detailedProductOptional = moderationService.getDetailedProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "Не найден товар с ID " + productId));
		}
		Product product = detailedProductOptional.get().getProduct();
		try {
			moderationService.updateAttributesAndCategory(product, category, attributeValues);
		} catch (PublicationException e) {
			log.warn(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/condition")
	public ResponseEntity<?> updateCondition(@Valid ConditionRequest conditionRequest,
											 BindingResult bindingResult, UserIdAuthenticationToken token) {
		checkAccessBeforeEdit(token, conditionRequest.getProductId());
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}
		Optional<DetailedProduct> detailedProductOptional = moderationService.getDetailedProduct(conditionRequest.productId);
		if (!detailedProductOptional.isPresent()) {
			return badResponseWithFieldError("Ошибка", "Не найден товар с ID " + conditionRequest.productId);
		}
		Product product = detailedProductOptional.get().getProduct();
		try {
			publicationService.updateCondition(product,
					conditionRequest.conditionId);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/marks")
	public ResponseEntity<?> updateMarks(
			@RequestParam Long productId,
			@RequestParam Boolean vintage,
			@RequestParam Boolean ourChoice,
			@RequestParam(value = "newCollection", required = false, defaultValue = "0") boolean isNewCollection,
			UserIdAuthenticationToken token) {
		checkAccessBeforeEdit(token, productId);
		if(token.hasAuthority(AuthorityName.PRODUCT_MODERATION)) {
			moderationService.updateMarks(productId, vintage, ourChoice, isNewCollection);
		}
		else {
			moderationService.updateMarks(productId, vintage, isNewCollection);
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/description")
	public ResponseEntity<?> updateDescription(
			@RequestParam Long productId,
			@RequestParam String description,
			@RequestParam(required = false) String origin,
			@RequestParam(required = false) BigDecimal purchasePrice,
			@RequestParam(required = false) Integer purchaseYear,
			@RequestParam(required = false) BigDecimal rrp,
			@RequestParam(required = false) String vendorCode,
			@RequestParam(required = false) String model,
			UserIdAuthenticationToken token

	) {
		checkAccessBeforeEdit(token, productId);
		Optional<DetailedProduct> detailedProduct = moderationService.getDetailedProduct(productId);
		if (!detailedProduct.isPresent()) {
			return badResponseWithFieldError("Ошибка", "Не найден товар с id: " + productId);
		}
		Product product = detailedProduct.get().getProduct();
		try {
			publicationService.updateOriginAndDescription(product, description, origin, purchasePrice, purchaseYear);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		moderationService.updateRrpPrice(productId, rrp);
		moderationService.updateVendorCode(productId, vendorCode);
		moderationService.updateModel(productId, model);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/brand")
	public ResponseEntity<?> updateBrand(@RequestParam Long productId, @RequestParam Long brandId, UserIdAuthenticationToken token) {
		checkAccessBeforeEdit(token, productId);
		moderationService.updateBrand(productId, brandId);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/items")
	public ResponseEntity<?> updateItems(@RequestBody @Valid ProductSizeMappingRequest request, BindingResult bindingResult) {

		productSizeMappingRequestValidator.validate(request, bindingResult);
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}
		try {
			moderationService.updateProductItemsCount(request.getProductId(), request.getProductSizeMappings());
		} catch (CommissionException e) {
			log.error(e.getLocalizedMessage() + ". ProductId: " + request.getProductId(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("error", "Ошибка при расчете комиссии"));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + ". ProductId: " + request.getProductId(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("error", "Произошла ошибка"));
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/sizeType")
	public ResponseEntity<?> updateSizeType(@RequestParam Long productId, @RequestParam SizeType sizeType, UserIdAuthenticationToken token) {
		checkAccessBeforeEdit(token, productId);
		Optional<DetailedProduct> detailedProductOptional = moderationService.getDetailedProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "Не найден товар с ID " + productId));
		}
		publicationService.updateSizeType(detailedProductOptional.get().getProduct(), sizeType);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/sendToModeration")
	public ResponseEntity<?> sendToModeration(@RequestParam Long productId, UserIdAuthenticationToken token) {
		checkAccessBeforeEdit(token, productId);
		Product product = productService.getProduct(productId).get().getProduct();
		try {
			publicationService.sendToModeration(product);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}


	@GetMapping("/publicationGridInfo")
	public ResponseEntity<?> getCategoryLeafs(@RequestParam Long categoryId) {
		ProPublicationGridInfo publicationGridInfo = proPublicationService.getPublicationGridInfo(categoryId);
		return ResponseEntity.ok(publicationGridInfo);
	}

	@PostMapping("/publish")
	public ResponseEntity<?> publishProductItems(UserIdAuthenticationToken token,
												 @RequestBody @Valid ProPublicationRequest request,
												 BindingResult bindingResult) {
		productSizeMappingRequestValidator.validate(request, bindingResult);
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}
		ErrorNotification errorNotification = new ErrorNotification();
		try {
			Product product = proPublicationService.publishProduct(request, token.getUserId(), errorNotification);
			if (product == null) {
				return ResponseEntity.badRequest().body(errorNotification.getAll());
			}
			return ResponseEntity.ok(product.getId());
		} catch (ProPublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
	}


	private void checkAccessBeforeEdit(UserIdAuthenticationToken token, Long productId) {
		Product product = productService.getProduct(productId).get().getProduct();
		if (!canUserEditProduct(token, product)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean canUserEditProduct(UserIdAuthenticationToken token, Product product) {
		return token.hasAuthority(AuthorityName.PRODUCT_MODERATION) ||
				product.getProductState().equals(ProductState.DRAFT) ||
				product.getProductState().equals(ProductState.NEED_MODERATION) ||
				product.getProductState().equals(ProductState.REJECTED);
	}

	@Data
	private static class ConditionRequest {
		@NotNull(message = "Не указано состояние товара")
		@Min(value = 1, message = "Не указано состояние товара")
		private Long conditionId;

		@NotNull(message = "Не указан идентификатор товара")
		private Long productId;
	}
}
