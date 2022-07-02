package su.reddot.domain.service.publication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.dao.product.ProductAttributeValueBindingRepository;
import su.reddot.domain.dao.product.ProductConditionRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.enums.AuthorityName;
import su.reddot.domain.model.product.*;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.controller.publication.PublicationPropertiesController;
import su.reddot.presentation.validation.DefaultImageValidator;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;
import static su.reddot.domain.model.product.ProductState.DRAFT;
import static su.reddot.domain.model.product.ProductState.SOLD;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {

	private static final long MAX_PHOTO_SIZE = 8 * 1024 * 1024;

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final SizeRepository sizeRepository;
	private final ProductAttributeValueBindingRepository productAttributeValueBindingRepository;
	private final AttributeValueRepository attributeValueRepository;
	private final ProductConditionRepository productConditionRepository;
	private final BrandService brandService;
	private final ProductItemService productItemService;
	private final ImageService imageService;
	private final ProductService productService;
	private final CategoryService categoryService;

	private final ApplicationEventPublisher pub;

	@Override
	public Product publish(PublicationRequest publicationRequest, Long userId) throws PublicationException {
		Category category = categoryRepository.findOne(publicationRequest.getChildCategory());
		if (category == null) {
			throw new IllegalArgumentException(
					"Категории с id " + publicationRequest.getChildCategory() + " не существует");
		}

		User user = userRepository.findOne(userId);
		if (user == null) {
			throw new IllegalArgumentException(
					"Пользователя с id " + userId + " не существует");
		}

		Optional<Brand> brand = brandService.findById(publicationRequest.getBrand());
		if (!brand.isPresent()) {
			throw new IllegalArgumentException(
					"Бренда с id " + publicationRequest.getBrand() + " не существует");
		}

		Product product = new Product();
		product.setBrand(brand.get());
		product.setCategory(category);
		product.setProductState(DRAFT);
		product.setSeller(user);

		try {
			productRepository.save(product);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			throw new PublicationException();
		}

		ProductItem productItem = new ProductItem();
		productItem.setProduct(product);
		productItem.setState(ProductItem.State.INITIAL);
		trySaveProductItem(productItem);
		return product;
	}

	private void trySaveProductItem(ProductItem productItem) throws PublicationException {
		try {
			productItemService.save(productItem);
		} catch (CommissionException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new PublicationException("Ошибка при расчете комиссии", e);
		}
	}

	@Override
	@Transactional
	public Product updateProductInfo(Product product, Long categoryId, SizeType sizeType, Long sizeId, List<Long> attributes) throws PublicationException {
		updateCategory(product, categoryId);
		updateSize(product, sizeType, sizeId);
		return updateAttributes(product, attributes);
	}

	@Override
	public Product updateCategory(Product product, Long categoryId) throws PublicationException {
		if (product.getProductState().equals(SOLD)) {
			throw new PublicationException("Нельзя изменять уже проданный товар. Id:" + product.getId());
		}

		Category category = categoryRepository.findOne(categoryId);
		if (category == null) {
			throw new PublicationException("Категория с ID: " + categoryId + " не найдена");
		}
		if (category.hasChildren()) {
			throw new PublicationException("Некорректная категория c ID: " + categoryId + ". Можно выбрать только конечную категорию");
		}
		product.setCategory(category);
		productRepository.save(product);
		return product;
	}

	@Override
	public Product updateProductDescription(Product product, String description, boolean isVintage, String model,
	                                        String origin,
	                                        BigDecimal purchasePrice,
	                                        Integer purchaseYear,
	                                        String serialNumber) throws PublicationException {
		ProductItem productItem = productItemService.findFirstByProduct(product);

		updateOriginAndDescription(product, description, origin, purchasePrice, purchaseYear);

		productItem.setSerialNumber(serialNumber);
		product.setModel(model);

		product.setVintage(isVintage);

		trySaveProductItem(productItem);
		productRepository.save(product);
		return product;
	}

	@Override
	public Product updateOriginAndDescription(Product product, String description, String origin, BigDecimal purchasePrice, Integer purchaseYear) throws PublicationException {
		if (description == null) {
			throw new PublicationException("Не указано описание товара");
		}
		product.setDescription(description);
		product.setOrigin(origin);
		product.setPurchaseYear(purchaseYear);
		product.setPurchasePrice(purchasePrice);
		productRepository.save(product);
		return product;
	}

	@Override
	public List<ProductCondition> getAllProductConditions() {
		return productConditionRepository.findAll(
				new Sort(Sort.Direction.ASC, "sortOrder"));
	}

	@Override
	public Product updateProductConditionAndPrice(Product product, Long conditionId, BigDecimal priceWithCommission) throws PublicationException {
		updateCondition(product, conditionId);
		return updatePrice(product, priceWithCommission);
	}

	@Override
	public Product updatePrice(Product product, BigDecimal priceWithCommission) throws PublicationException {
		if (priceWithCommission == null) {
			throw new PublicationException("Не указана цена товара");
		}
		ProductItem productItem = productItemService.findFirstByProduct(product);
		productItem.setStartPrice(priceWithCommission);
		productItem.setCurrentPrice(priceWithCommission);
		trySaveProductItem(productItem);
		return productRepository.save(product);
	}

	@Override
	public Product setDiscount(Product product, BigDecimal priceWithDiscount) throws PublicationException {
		if (priceWithDiscount == null) {
			throw new PublicationException("Не указана цена товара");
		}
		ProductItem productItem = productItemService.findFirstByProduct(product);
		productItem.setCurrentPrice(priceWithDiscount);
		trySaveProductItem(productItem);

		pub.publishEvent(new PriceChangedEvent(product));

		return productRepository.save(product);
	}

	@Override
	public Product updateCondition(Product product, Long conditionId) throws PublicationException {
		if (conditionId == null) {
			throw new PublicationException("Не указано состояние товара");
		}

		ProductCondition productCondition = productConditionRepository.findOne(conditionId);
		if (productCondition == null) {
			throw new PublicationException("Не указано состояние товара");
		}
		product.setProductCondition(productCondition);
		return productRepository.save(product);
	}

	@Override
	public Product sendToModeration(Product product) throws PublicationException {
		ProductItem productItem = productItemService.findFirstByProduct(product);
		if (product.getProductState().equals(SOLD)) {
			throw new PublicationException("Нельзя поставить на модерацию уже проданный товар!");
		}

		/*
			Нужны проверки:
			1) выбрана конечная категория, выбраны атрибуты
			2) TODO: выбран размер
			3) указано описание
			4) залиты фотки
			5) указано состояние товара, указана цена товара
			6) указаны данные продавца
		 */

		//1
		if (!product.getCategory().isLeaf()) {
			throw new PublicationException("Выбрана не конечная категория");
		}
		List<AttributeValue> attributeValuesByProductWithLimit = productAttributeValueBindingRepository.findAttributeValuesByProductWithLimit(product, 10);
		if (attributeValuesByProductWithLimit.isEmpty()) {
			throw new PublicationException("Не выбраны атрибуты");
		}

		//2
		if(!isAttributesSubmittedCorrectly(product)) {
			throw new PublicationException("Выбраны не все атрибуты");
		}
		if(!isSizeSubmittedCorrectly(product)) {
			throw new PublicationException("Не выбран размер");
		}

		//3
		boolean isDescriptionSubmitted = isDescriptionSubmitted(product);
		if (!isDescriptionSubmitted) {
			throw new PublicationException("Не указано описание товара");
		}

		//4
		Optional<ProductImage> firstPhoto = imageService.getImageByProductAndPhotoOrder(product, 1);
		Optional<ProductImage> secondPhoto = imageService.getImageByProductAndPhotoOrder(product, 2);
		if (!firstPhoto.isPresent()) {
			throw new PublicationException("Не загружена главная фотография товара");
		}
		if (!secondPhoto.isPresent()) {
			throw new PublicationException("Не загружена вторая фотография товара");
		}


		//5
		if (product.getProductCondition() == null) {
			throw new PublicationException("Не указано состояние товара");
		}
		if (productItem.getCurrentPriceWithoutCommission() == null) {
			throw new PublicationException("Не указана цена товара");
		}

		//6
		checkSellerRequisite(product);

		product.setProductState(ProductState.NEED_MODERATION);
		product.setSendToModeratorTime(ZonedDateTime.now(ZoneId.systemDefault()));
		return productRepository.save(product);
	}

	private void checkSellerRequisite(Product product) throws PublicationException {
		SellerRequisite sellerRequisite = product.getSeller().getSellerRequisite();
		if (sellerRequisite == null) { throw new PublicationException("Не указан номер телефона"); }

		if (isEmptyOrWhitespace(sellerRequisite.getPhone())) {
			throw new PublicationException("Не указан номер телефона продавца");
		}
		if (isEmptyOrWhitespace(sellerRequisite.getAddress())) {
			throw new PublicationException("Не указан адрес продавца");
		}
		if (isEmptyOrWhitespace(sellerRequisite.getCity())) {
			throw new PublicationException("Не указан город продавца");
		}
		if (isEmptyOrWhitespace(sellerRequisite.getFirstName())) {
			throw new PublicationException("Не указано имя продавца");
		}
		if (isEmptyOrWhitespace(sellerRequisite.getLastName())) {
			throw new PublicationException("Не указана фамилия продавца");
		}
		if (isEmptyOrWhitespace(sellerRequisite.getZipCode())) {
			throw new PublicationException("Не указан почтовый индекс продавца");
		}
	}

	private boolean isDescriptionSubmitted(Product product) {
		String description = product.getDescription();
		return !isEmptyOrWhitespace(description);
	}

	@Override
	public void updateProductPhoto(Product product, MultipartFile photo, int photoOrder) throws PublicationException {
		if (photoOrder < 1 || photoOrder > 4) {
			throw new PublicationException("Некорректный номер фотографии товара.");
		}


		Optional<ProductImage> oldPhotoOptional = imageService.getImageByProductAndPhotoOrder(product, photoOrder);
		oldPhotoOptional.ifPresent(productImage -> imageService.deleteImage(productImage.getId()));
		imageService.saveImageByProduct(product, photo, photoOrder);
	}

	@Override
	public void updateSellerInfo(Product product, String phone, String firstName,
								 String lastName, String postcode, String city, String address, String extensiveAddress) {

		User seller = product.getSeller();
		SellerRequisite sellerRequisite = seller.getSellerRequisite();
		if (sellerRequisite == null) {
			sellerRequisite = new SellerRequisite();
			seller.setSellerRequisite(sellerRequisite);
		}

        sellerRequisite.setPhone(phone)
        	.setFirstName(firstName)
        	.setLastName(lastName)
        	.setZipCode(postcode)
        	.setCity(city)
        	.setAddress(address);

		if (extensiveAddress != null) {
			sellerRequisite.setExtensiveAddress(extensiveAddress);
		}

		userRepository.save(seller);
	}

	@Override
	public boolean userHasDrafts(Long userId) {
		List<Product> productsByUser = productRepository.findProductsBySellerIdAndProductState(userId, DRAFT);
		return !productsByUser.isEmpty();
	}

	@Override
	public Product updateAttributes(Product product, List<Long> attributes) {
		/*
		TODO: вот тут есть сложность. При обновлении товаров мы добавляем атрибуты в таблицу, и там стоит UNIQUE.
		Самый простой вариант это удалить все ранее установленные атрибуты
		(т.к. для обновления значений атрибутов нужно будет проверять тип атрибута, а потом обновлять значение)
		 */
		productAttributeValueBindingRepository.deleteByProduct(product);
		productAttributeValueBindingRepository.flush();

		if (attributes != null) {
			attributes.forEach(attributeValueId -> {
				AttributeValue attributeValue = attributeValueRepository.findOne(attributeValueId);
				if (attributeValue != null) {
					ProductAttributeValueBinding binding = new ProductAttributeValueBinding();
					binding.setProduct(product);
					binding.setAttributeValue(attributeValue);
					productAttributeValueBindingRepository.save(binding);
				} else {
					log.error("Некорректный атрибут с ID: " + attributeValueId);
				}
			});
		}

		productRepository.save(product);
		return product;
	}

	@Override
	public Product updateSize(Product product, SizeType sizeType, Long sizeId) throws PublicationException {
		ProductItem productItem = productItemService.findFirstByProduct(product);
		updateSizeType(product, sizeType);
		//TODO: захуярить размеры
		if (sizeId != null) {
			Size size = sizeRepository.findOne(sizeId);
			productItem.setSize(size);
			trySaveProductItem(productItem);
		}
		return product;
	}

	@Override
	public Product updateSizeType(Product product, SizeType sizeType) {
		product.setSizeType(sizeType);
		productRepository.save(product);
		return product;
	}

	@Override
	public ResponseEntity<?> updatePhoto(Long productId, PublicationPropertiesController.PublicationPhotoRequest imageContainer, BindingResult bindingResult, UserIdAuthenticationToken token) {
		Optional<DetailedProduct> detailedProductOptional = productService.getProduct(productId);
		if (!detailedProductOptional.isPresent()) {
			return Utils.badResponseWithFieldError("Ошибка", "Товар с ID: " + productId + " не найден");
		}
		Product product = detailedProductOptional.get().getProduct();
		if (!productBelongsToUser(product, token.getUserId()) && !token.hasAuthority(AuthorityName.PRODUCT_MODERATION)) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "У вас нет доступа к данному товару"));
		}

		DefaultImageValidator imageValidator = new DefaultImageValidator(MAX_PHOTO_SIZE);
		imageValidator.validate(imageContainer.getImage(), bindingResult);

		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}

		try {
			this.updateProductPhoto(product, imageContainer.getImage(), imageContainer.getPhotoOrder());
		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
		}

		return ResponseEntity.ok().build();
	}

	public boolean productBelongsToUser(Product product, Long userId) {
		return product.getSeller().getId().equals(userId);
	}

	@Override
	public boolean runProPublication(Long userId) {
		if (userId == null) {return false;}

		User user = userRepository.findOne(userId);
		if(user != null){
			return user.isPro();
		}
		return false;
	}

	@Override
	public List<Integer> getCompletedSteps(Product p) {
		List<Integer> completedSteps = new ArrayList<>();

		boolean isAttributesSubmittedCorrectly = isAttributesSubmittedCorrectly(p);
		boolean isSizeSubmittedCorrectly = isSizeSubmittedCorrectly(p);
		if(isAttributesSubmittedCorrectly && isSizeSubmittedCorrectly) {
			completedSteps.add(1);
		}

		Optional<ProductImage> firstPhoto = imageService.getImageByProductAndPhotoOrder(p, 1);
		Optional<ProductImage> secondPhoto = imageService.getImageByProductAndPhotoOrder(p, 2);
		boolean requiredPhotosLoaded = firstPhoto.isPresent() && secondPhoto.isPresent();
		if(requiredPhotosLoaded) {
			completedSteps.add(2);
		}

		if(isDescriptionSubmitted(p)) {
			completedSteps.add(3);
		}

		if(p.getProductCondition() != null && Optional.ofNullable(productItemService.findFirstByProduct(p)).map(ProductItem::getCurrentPriceWithoutCommission).isPresent()) {
			completedSteps.add(4);
		}

		try {
			checkSellerRequisite(p);
			completedSteps.add(5);
		} catch (PublicationException e) {
			log.info(e.getLocalizedMessage(), e);
		}

		return completedSteps;

	}

	private boolean isSizeSubmittedCorrectly(Product p) {
		return p.getSizeType() != null && Optional.ofNullable(productItemService.findFirstByProduct(p)).map(ProductItem::getSize).isPresent();
	}

	private boolean isAttributesSubmittedCorrectly(Product p) {
		Set<Long> requiredAttributes = categoryService.getAllAttributes(p.getCategory().getId()).stream().map(a -> a.getAttribute().getId()).collect(Collectors.toSet());
		Set<Long> existedAttributes = Optional.ofNullable(p.getAttributeValues()).orElse(Collections.emptyList()).stream().map(a -> a.getAttributeValue().getAttribute().getId()).collect(Collectors.toSet());
		return requiredAttributes.equals(existedAttributes);
	}
}
