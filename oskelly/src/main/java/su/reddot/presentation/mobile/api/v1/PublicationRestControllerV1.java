package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.category.PublicationPhotoSample;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.domain.service.publication.info.PublicationInfoService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.controller.publication.SellerRequest;
import su.reddot.presentation.mobile.api.v1.request.PublicationRequest;
import su.reddot.presentation.mobile.api.v1.request.SelectedAttributeValue;
import su.reddot.presentation.mobile.api.v1.response.AttributeValue;
import su.reddot.presentation.mobile.api.v1.response.PublicationResponse;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static su.reddot.domain.model.product.ProductState.DRAFT;

/**
 * @author Vitaliy Khludeev on 05.09.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/publication")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class PublicationRestControllerV1 {

	private final ProductService productService;
	private final PublicationService publicationService;
	private final CategoryService categoryService;
	private final ProductItemService productItemService;
	private final ImageService imageService;
	private final CommissionService commissionService;
	private final UserService userService;
	private final CategoryRepository categoryRepository;
	private final BrandService brandService;
	private final ProductRepository productRepository;
	private final PublicationInfoService publicationInfoService;

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;

	@GetMapping("/drafts")
	public ResponseEntity<List<PublicationResponse>> getDrafts(UserIdAuthenticationToken token) {
		ProductService.FilterSpecification filterSpecification = new ProductService.FilterSpecification()
				.sellerId(token.getUserId())
				.state(ProductState.DRAFT);
		ProductsList productsList = productService.getProductsList(filterSpecification, 1, ProductService.SortAttribute.ID);
		return ResponseEntity.ok(productsList.getProducts().stream().map(p -> getPublicationResponseForProduct(token, p.getId())).collect(Collectors.toList()));
	}

	@GetMapping(value = "/product/{productId}")
	public ResponseEntity<PublicationResponse> getPublicationInfo(UserIdAuthenticationToken token, @PathVariable Long productId) throws CommissionException {
		PublicationResponse response = getPublicationResponseForProduct(token, productId);
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/commission/{productId}")
	public ResponseEntity<BigDecimal> getPriceWithoutCommission(UserIdAuthenticationToken token, @PathVariable Long productId, @RequestParam BigDecimal price) throws CommissionException {
		Product product = productService.getProduct(productId).orElseThrow(IllegalArgumentException::new).getProduct();
		if(!product.getSeller().getId().equals(token.getUserId())) {
			throw new IllegalArgumentException("Товар вам не принадлежит");
		}
		BigDecimal priceWithoutCommission = commissionService.calculatePriceWithoutCommission(
				price, product.getSeller(), product.getCategory(), false, false)
				.setScale(2, RoundingMode.UP);
		return ResponseEntity.ok(priceWithoutCommission);
	}

	@PostMapping("/step/1")
	@Transactional
	public ResponseEntity<PublicationResponse> firstStep(@RequestParam Long brandId, @RequestParam Long categoryId, UserIdAuthenticationToken token) throws CommissionException {
		Product product = new Product();
		return firstStep(brandId, categoryId, token, product);
	}

	@PutMapping("/step/1")
	@Transactional
	public ResponseEntity<PublicationResponse> firstStep(@RequestParam Long productId, @RequestParam Long brandId, @RequestParam Long categoryId, UserIdAuthenticationToken token) throws CommissionException {
		Product product = validateProduct(productId, token);
		return firstStep(brandId, categoryId, token, product);
	}

	@PutMapping("/step/2")
	@Transactional
	public ResponseEntity<PublicationResponse> secondStep(
			@RequestParam Long productId,
			@RequestParam List<Long> attributes,
			@RequestParam SizeType sizeType,
			@RequestParam Long sizeId,
			UserIdAuthenticationToken token
	) throws CommissionException, PublicationException {
		Product product = validateProduct(productId, token);
		publicationService.updateAttributes(product, attributes);
		publicationService.updateSize(product, sizeType, sizeId);
		return ResponseEntity.ok(getPublicationResponseForProduct(token, product.getId()));
	}

	@PostMapping("/step/3")
	@Transactional
	public ResponseEntity<PublicationResponse> thirdStep(
			MultipartFile image,
			@RequestParam Long productId,
			@RequestParam Integer order,
			UserIdAuthenticationToken token
	) throws CommissionException, PublicationException {
		if(image == null) {
			throw new IllegalArgumentException("Фото не указано");
		}
		publicationService.updateProductPhoto(validateProduct(productId, token), image, order);
		return ResponseEntity.ok(getPublicationResponseForProduct(token, productId));
	}

	@PutMapping("/step/4")
	@Transactional
	public ResponseEntity<PublicationResponse> fourthStep(
			@RequestParam Long productId,
			@RequestParam String description,
			@RequestParam boolean vintage,
			@RequestParam(required = false) String model,
			@RequestParam(required = false) String origin,
			@RequestParam(required = false) BigDecimal purchasePrice,
			@RequestParam(required = false) Integer purchaseYear,
			@RequestParam(required = false) String serialNumber,
			UserIdAuthenticationToken token
	) throws CommissionException, PublicationException {
		Product product = validateProduct(productId, token);
		publicationService.updateProductDescription(product, description, vintage, model, origin, purchasePrice, purchaseYear, serialNumber);
		return ResponseEntity.ok(getPublicationResponseForProduct(token, product.getId()));
	}

	@PutMapping("/step/5")
	@Transactional
	public ResponseEntity<PublicationResponse> fifthStep(
			@RequestParam Long productId,
			@RequestParam Long conditionId,
			@RequestParam BigDecimal price,
			UserIdAuthenticationToken token
	) throws CommissionException, PublicationException {
		Product product = validateProduct(productId, token);
		publicationService.updateProductConditionAndPrice(product, conditionId, price);
		return ResponseEntity.ok(getPublicationResponseForProduct(token, product.getId()));
	}

	@PutMapping("/step/6")
	@Transactional
	public ResponseEntity<?> sixthStep(
			@RequestParam Long productId,
			@Valid SellerRequest request,
			BindingResult bindingResult,
			UserIdAuthenticationToken token
	) throws CommissionException, PublicationException {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}
		Product product = validateProduct(productId, token);
		publicationService.updateSellerInfo(
				product, request.getPhone(), request.getFirstName(), request.getLastName(),
				request.getPostcode(), request.getCity(), request.getAddress(), request.getExtensiveAddress()
		);
		if(request.isCompletePublication()) {
			publicationService.sendToModeration(product);
		}
		return ResponseEntity.ok(getPublicationResponseForProduct(token, product.getId()));
	}

	@PutMapping("/publish")
	@Transactional
	public ResponseEntity<PublicationResponse> publish(@RequestBody PublicationRequest r, UserIdAuthenticationToken token) throws PublicationException, CommissionException {
		Product p = validateProduct(r.getId(), token);

		publicationService.updateAttributes(p, r.getSelectedAttributeValues().stream().map(SelectedAttributeValue::getId).collect(Collectors.toList()));
		
		try {
			publicationService.updateSize(p, r.getSelectedSizeType(), r.getSelectedSizeId());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		try {
			publicationService.updateProductDescription(p, r.getDescription(), r.isVintage(), r.getModel(), r.getOrigin(), r.getPurchasePrice(), r.getPurchaseYear(), r.getSerialNumber());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		try {
			publicationService.updateProductConditionAndPrice(p, r.getSelectedCondition(), r.getPriceWithCommission());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		publicationService.updateSellerInfo(
				p, r.getSellerRequisite().getPhone(), r.getSellerRequisite().getFirstName(), r.getSellerRequisite().getLastName(),
				r.getSellerRequisite().getZipCode(), r.getSellerRequisite().getCity(), r.getSellerRequisite().getAddress(), null
		);
		if(r.isCompletePublication()) {
			publicationService.sendToModeration(p);
		}
		return ResponseEntity.ok(getPublicationResponseForProduct(token, p.getId()));
	}

	@PutMapping("/publish2")
	@Transactional
	public ResponseEntity<PublicationResponse> publish(@RequestBody PublicationResponse r, UserIdAuthenticationToken token) throws CommissionException, PublicationException {
		Product p = validateProduct(r.getId(), token);

		publicationService.updateAttributes(p, r.getSelectedAttributeValues().stream().map(AttributeValue::getId).collect(Collectors.toList()));

		try {
			publicationService.updateSize(p, r.getSelectedSizeType(), r.getSelectedSize().getId());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		try {
			publicationService.updateProductDescription(p, r.getDescription(), r.isVintage(), r.getModel(), r.getOrigin(), r.getPurchasePrice(), r.getPurchaseYear(), r.getSerialNumber());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		try {
			publicationService.updateCondition(p, r.getSelectedCondition());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		try {
			publicationService.updatePrice(p, r.getPriceWithCommission());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		publicationService.updateSellerInfo(
				p, r.getSellerRequisite().getPhone(), r.getSellerRequisite().getFirstName(), r.getSellerRequisite().getLastName(),
				r.getSellerRequisite().getZipCode(), r.getSellerRequisite().getCity(), r.getSellerRequisite().getAddress(), null
		);
		if(r.getIsCompletePublication() != null && r.getIsCompletePublication()) {
			publicationService.sendToModeration(p);
		}
		return ResponseEntity.ok(getPublicationResponseForProduct(token, p.getId()));
	}

	@DeleteMapping("/{draftId}")
	public ResponseEntity<?> deleteDraft(@PathVariable Long draftId, UserIdAuthenticationToken t) {
		Product p = productService.getProduct(draftId).map(DetailedProduct::getProduct).orElseThrow(IllegalArgumentException::new);
		User u = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		if(!u.equals(p.getSeller())) {
			throw new AccessDeniedException("Товар вам не принадлежит");
		}
		if(!DRAFT.equals(p.getProductState())) {
			throw new IllegalArgumentException("Это не черновик");
		}
		productService.delete(p);
		return ResponseEntity.ok().build();
	}

	private Product validateProduct(@RequestParam Long productId, UserIdAuthenticationToken token) {
		Optional<DetailedProduct> product = productService.getProduct(productId);
		if(!product.isPresent()) {
			throw new IllegalArgumentException("Товар не найден");
		}
		if(!publicationService.productBelongsToUser(product.get().getProduct(), token.getUserId())) {
			throw new IllegalArgumentException("Товар вам не принадлежит");
		}
		if(product.get().getProduct().getProductState() != null && product.get().getProduct().getProductState() != DRAFT) {
			throw new IllegalArgumentException("Это не черновик");
		}
		return product.get().getProduct();
	}


	private ResponseEntity<PublicationResponse> firstStep(Long brandId, Long categoryId, UserIdAuthenticationToken token, Product product) throws CommissionException {
		Category category = categoryRepository.findOne(categoryId);
		if (category == null) {
			throw new IllegalArgumentException(
					"Категории с id " + categoryId + " не существует");
		}
		if(category.hasChildren()) {
			throw new IllegalArgumentException(
					"Категория " + categoryId + " не является листом");
		}
		Optional<Brand> brand = brandService.findById(brandId);
		if (!brand.isPresent()) {
			throw new IllegalArgumentException(
					"Бренда с id " + brandId + " не существует");
		}
		User user = userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new);
		product.setBrand(brand.get());
		product.setCategory(category);
		product.setProductState(DRAFT);
		product.setSeller(user);

		productRepository.save(product);
		ProductItem productItem = new ProductItem();
		productItem.setProduct(product);
		productItem.setState(ProductItem.State.INITIAL);
		productItemService.save(productItem);
		return ResponseEntity.ok(getPublicationResponseForProduct(token, product.getId()));
	}

	private PublicationResponse getPublicationResponseForProduct(UserIdAuthenticationToken token, Long productId) {
		Optional<DetailedProduct> detailedProductOpt = productService.getProduct(productId);
		DetailedProduct detailedProduct = detailedProductOpt.orElseThrow(() -> new IllegalArgumentException("Товар не найден"));
		if(!publicationService.productBelongsToUser(detailedProduct.getProduct(), token.getUserId())) {
			throw new IllegalArgumentException("Товар вам не принадлежит");
		}
		Product product = detailedProduct.getProduct();
		List<Long> selectedCategories = new ArrayList<>();
		List<su.reddot.presentation.mobile.api.v1.response.AttributeValue> selectedAttributeValues = detailedProduct.getAttributeValues().stream().map(v -> new su.reddot.presentation.mobile.api.v1.response.AttributeValue(v.getId(), v.getAttribute().getId(), v.getAttribute().getName(), v.getValue())).collect(Collectors.toList());
		if(product.getCategory() != null) {
			selectedCategories = categoryService.getAllParentCategories(product.getCategory().getId()).stream().map(CatalogCategory::getId).collect(Collectors.toList());
			selectedCategories.add(product.getCategory().getId());
		}
		ProductItem firstProductItem = productItemService.findFirstByProduct(product);
		BigDecimal commission;
		try {
			commission = commissionService.calculateCommission(firstProductItem);
		} catch (CommissionException e) {
			throw new IllegalStateException(e);
		}
		return new PublicationResponse(
				product.getId(),
				selectedCategories,
				product.getCategory().getId(),
				product.getCategory().getDisplayName(),
				product.getBrand() != null ? product.getBrand().getId() : null,
				product.getBrand() != null ? product.getBrand().getName() : null,
				selectedAttributeValues,
				product.getSizeType(),
				firstProductItem.getSize() != null ? new PublicationResponse.SizeValue(firstProductItem.getSize().getId(), firstProductItem.getSize().getBySizeType(product.getSizeType())) : null,
				imageService.getProductImages(product).stream().map(i -> new PublicationResponse.Image(i.getLargeImageUrl(), i.getPhotoOrder())).collect(Collectors.toList()),
				product.getDescription(),
				product.isVintage(),
				product.getModel(),
				product.getOrigin(),
				product.getPurchasePrice(),
				product.getPurchaseYear(),
				firstProductItem.getSerialNumber(),
				(product.getProductCondition() != null) ? product.getProductCondition().getId() : null,
				commission,
				firstProductItem.getCurrentPrice(),
				firstProductItem.getCurrentPriceWithoutCommission(),
				product.getSeller().getSellerRequisite(),
				false,
				publicationInfoService.getPhotoSamplesForCategory(product.getCategory().getId()).stream().map(s -> new PublicationPhotoSample().setImagePath(urlPrefix + s.getImagePath()).setPhotoOrder(s.getPhotoOrder())).collect(Collectors.toList()),
				publicationService.getCompletedSteps(product)
		);
	}
}
