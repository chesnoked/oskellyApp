package su.reddot.presentation.controller.publication;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.model.category.PublicationPhotoSample;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.domain.service.publication.info.PublicationInfoService;
import su.reddot.domain.service.publication.info.view.PublicationInfoView;
import su.reddot.domain.service.publication.info.view.PublicationSizeTypeView;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Controller
@RequestMapping("/publication/properties")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class PublicationPropertiesController {

	private final CategoryService        categoryService;
	private final PublicationService     publicationService;
	private final PublicationInfoService publicationInfoService;
	private final ProductService         productService;
	private final CommissionService      commissionService;

	private final TemplateEngine templateEngine;

	@Value("${resources.images.urlPrefix}")
	private String urlPrefix;

	@GetMapping("/{productId}")
	public String properties(@PathVariable Long productId, Model model, UserIdAuthenticationToken token) {

		Optional<DetailedProduct> productOptional = productService.getProduct(productId);
		if (!productOptional.isPresent()) {
			model.addAttribute("errorMessage", "Товар не найден");
			return "oops";
		}
		DetailedProduct detailedProduct = productOptional.get();

		//Если товар не принадлежит пользователю
		if (!publicationService.productBelongsToUser(detailedProduct.getProduct(), token.getUserId())) {
			model.addAttribute("errorMessage", "У вас нет доступа к данному товару");
			return "oops";
		}
		PublicationInfoView publicationInfoView = publicationInfoService.getPublicationInfoForProduct(detailedProduct);
		List<PublicationPhotoSample> samplesList = publicationInfoService.getPhotoSamplesForCategory(
				detailedProduct.getProduct().getCategory().getId());

		model.addAttribute("publicationInfo", publicationInfoView);
		model.addAttribute("photoSamplesCount", samplesList.size());
		model.addAttribute("photoSamples", samplesList);
		model.addAttribute("urlPrefix", urlPrefix);
		return "publication-properties";
	}

	@GetMapping("/attributes")
	public String loadAttributesForCategory(@RequestParam Long categoryId, Model model) {
		//возвращает либо субкатегории, либо атрибуты+размер (сразу завернутые в html)
		PublicationInfoView publicationInfoView = publicationInfoService
				.getSubcategoryOrAttributesForSelectedCategory(categoryId);

		model.addAttribute("publicationInfo", publicationInfoView);
		return "publication/fragments/attributeGroups";
	}

	@GetMapping("/category-photo-samples")
    @ResponseBody
	public PhotoSampleResponse categoryPhotoSamples(@RequestParam Long categoryId) {

		List<PublicationPhotoSample> rawPhotoSamples = publicationInfoService.getPhotoSamplesForCategory(categoryId);
		List<String> cookedPhotoSamples = cookPhotoSamples(rawPhotoSamples);

		return new PhotoSampleResponse().setSamples(cookedPhotoSamples);
	}

    private List<String> cookPhotoSamples(List<PublicationPhotoSample> rawPhotoSamples) {

        Context context = new Context();
        ArrayList<String> cookedPhotoSamples = new ArrayList<>();
        for (PublicationPhotoSample rawPhotoSample : rawPhotoSamples) {

            context.setVariable("sourceUrl", urlPrefix + rawPhotoSample.getImagePath());
            String cookedPhotoSample = templateEngine.process(
                    "publication/fragments/photo-sample",
                    new HashSet<>(Collections.singletonList("[th:fragment='photo-sample (sourceUrl)']")),
                    context );

            cookedPhotoSamples.add(cookedPhotoSample);
        }

        return cookedPhotoSamples;
    }

    @GetMapping("/sizetypes")
	public ResponseEntity<?> loadSizeTypesForCategory(@RequestParam Long categoryId) {
		List<PublicationSizeTypeView> sizeTypesForCategory = publicationInfoService.getSizeTypesForCategory(categoryId);
		return ResponseEntity.ok(sizeTypesForCategory);
	}

	//TODO: метод пересен в presentation/api/v1/SizeApi. Нужно выпилить метод и поменять ссылки на фронте
	@GetMapping("/sizes")
	public ResponseEntity<?> loadSizesForCategoryAndSizeType(@RequestParam Long categoryId,
	                                                         @RequestParam String sizeType) {
		SizeType sizeTypeEnum;
		try {
			sizeTypeEnum = SizeType.valueOf(sizeType);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Размер", "Некорректный тип размера"));
		}

		List<CatalogSize> sizesGroupedBySizeType = categoryService.getSizesGroupedBySizeType(categoryId);
		Optional<CatalogSize> catalogSizeOpt = sizesGroupedBySizeType.stream().filter(s -> s.getSizeType().equals(sizeTypeEnum)).findFirst();
		return ResponseEntity.ok(catalogSizeOpt.map(CatalogSize::getValues).orElseGet(Collections::emptyList));
	}

	@PutMapping("/{productId}/info")
	public ResponseEntity<?> submitDetailInfo(@PathVariable Long productId,
	                                          @RequestParam Long category,
	                                          @RequestParam SizeType sizeType,
	                                          @RequestParam(required = false) Long size,
	                                          @RequestParam(value = "attributeValues[]", required = false) List<Long> attributeValues,
	                                          UserIdAuthenticationToken token) {
		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}

		Product product = detailedProductOptional.get().getProduct();

		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}
		try {
			publicationService.updateProductInfo(product, category, sizeType, size, attributeValues);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{productId}/photo")
	public ResponseEntity<?> updatePhoto(@PathVariable Long productId,
	                                     @ModelAttribute PublicationPhotoRequest imageContainer,
	                                     BindingResult bindingResult, UserIdAuthenticationToken token) {
		return publicationService.updatePhoto(productId, imageContainer, bindingResult, token);
	}

	@Data
	public static class PublicationPhotoRequest {
		private MultipartFile image;
		private Integer photoOrder;
	}

	@PutMapping("{productId}/description")
	public ResponseEntity<?> updateDescription(@PathVariable Long productId,
	                                           @RequestParam String description,
	                                           @RequestParam boolean vintage,
	                                           @RequestParam(required = false) String model,
	                                           @RequestParam(required = false) String origin,
	                                           @RequestParam(required = false) BigDecimal purchasePrice,
	                                           @RequestParam(required = false) Integer purchaseYear,
	                                           @RequestParam(required = false) String serialNumber,
	                                           UserIdAuthenticationToken token) {
		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}

		try {
			publicationService.updateProductDescription(
					product, description, vintage, model, origin, purchasePrice, purchaseYear, serialNumber
			);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/{productId}/commission")
	public ResponseEntity<?> getPriceWithCommission(
			@PathVariable Long productId,
			@RequestParam BigDecimal price,
			UserIdAuthenticationToken token
	) {
		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}
		if(price == null) {
			return ResponseEntity.ok(BigDecimal.ZERO);
		}
		try {
			return ResponseEntity.ok(commissionService.calculatePriceWithoutCommission(
					price, product.getSeller(), product.getCategory(), false, false)
					.setScale(2, RoundingMode.UP).toString());
		} catch (CommissionException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "Ошибка при расчете комиссии"));
		}
	}

	@PutMapping("/{productId}/condition")
	public ResponseEntity<?> updateCondition(@PathVariable Long productId,
	                                         @Valid ConditionRequest conditionRequest,
	                                         BindingResult bindingResult, UserIdAuthenticationToken token) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}

		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}

		try {
			publicationService.updateProductConditionAndPrice(product,
					conditionRequest.conditionId,
					conditionRequest.price);
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{productId}/seller")
	public ResponseEntity<?> updateSeller(@PathVariable Long productId,
	                                      @Valid SellerRequest request,
	                                      BindingResult bindingResult, UserIdAuthenticationToken token) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}

		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}

		publicationService.updateSellerInfo(
				product, request.getPhone(), request.getFirstName(), request.getLastName(),
				request.getPostcode(), request.getCity(), request.getAddress(), request.getExtensiveAddress()
		);
		if (request.isCompletePublication()) {
			return sendToModeration(productId, token);
		} else {
			return ResponseEntity.ok().build();
		}
	}

	@PostMapping("/{productId}/moderate")
	public ResponseEntity<?> sendToModeration(@PathVariable Long productId, UserIdAuthenticationToken token) {

		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!publicationService.productBelongsToUser(product, token.getUserId())) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}

		try {
			publicationService.sendToModeration(product);
			return ResponseEntity.ok().build();
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}
	}

	@Data
	private static class ConditionRequest {
		@Digits(integer = 8, fraction = 2,
				message = "Цена может состоять не более чем из 8 цифр до запятой и 2 цифр после")
		@NotNull(message = "Не указана цена")
		@Min(value = 1, message = "Минимальная цена = 1")
		private BigDecimal price;

		@NotNull(message = "Не указано состояние товара")
		@Min(value = 1, message = "Не указано состояние товара")
		private Long conditionId;
	}

	@Getter @Setter @Accessors(chain = true)
	private static class PhotoSampleResponse {
		private List<String> samples;
	}
}
