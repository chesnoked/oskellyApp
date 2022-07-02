package su.reddot.domain.service.publication.info;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.category.PublicationPhotoSampleRepository;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.category.PublicationPhotoSample;
import su.reddot.domain.model.category.QPublicationPhotoSample;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.info.view.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class PublicationInfoServiceImpl implements PublicationInfoService {

	private final CategoryService    categoryService;
	private final PublicationService publicationService;
	private final ProductItemService productItemService;
	private final ImageService       imageService;
	private final PublicationPhotoSampleRepository photoSampleRepository;

	@Override
	public PublicationInfoView getPublicationInfoForProduct(DetailedProduct detailedProduct) {
		Product product = detailedProduct.getProduct();
		Category currentCategory = product.getCategory();
		ProductItem productItem = productItemService.findFirstByProduct(product);

		PublicationInfoView publicationInfoView;

		/*
			Здесь мы получаем те параметры, которые могут влиять то, какие блоки будут отображены на странице публикации
			(например, категории на странице блока инфо)
		 */
		if (publicationAttributesWasSavedFor(detailedProduct)) {
			publicationInfoView = getSavedPublicationInfo(detailedProduct, product, productItem, currentCategory);
		} else {
			publicationInfoView = getNotSavedPublicationInfo(currentCategory);
		}

		/*
			Здесь мы получаем те параметры, которые не будут изменять блоки страницы публикации
			(например, описание товара или параметры продавца)
		 */

		publicationInfoView.setProductConditions(getProductConditions(product));
		publicationInfoView.setId(product.getId());
		publicationInfoView.setBrand(product.getBrand().getName());
		publicationInfoView.setVintage(product.isVintage());
		publicationInfoView.setNewCollection(product.isNewCollection());
		publicationInfoView.setDescription(product.getDescription());
		publicationInfoView.setPrice(productItem.getCurrentPriceWithoutCommission() != null ?productItem.getCurrentPriceWithoutCommission().setScale(0, BigDecimal.ROUND_DOWN) : null);
		publicationInfoView.setPriceWithCommission(productItem.getCurrentPrice() != null ? productItem.getCurrentPrice().setScale(0, BigDecimal.ROUND_UP) : null);
		publicationInfoView.setModel(product.getModel());
		publicationInfoView.setVendorCode(product.getVendorCode());
		publicationInfoView.setRrpPrice(product.getRrpPrice());
		publicationInfoView.setPurchasePrice(product.getPurchasePrice());
		publicationInfoView.setPurchaseYear(product.getPurchaseYear());
		publicationInfoView.setOrigin(product.getOrigin());
		publicationInfoView.setSerialNumber(productItem.getSerialNumber());
		publicationInfoView.setOurChoice(product.getOurChoiceStatusTime() != null);

		if (product.getPublishTime() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
			DateTimeFormatter zoneOffsetFormatter = DateTimeFormatter.ofPattern("X");
			String formattedDateTime = product.getPublishTime().atZone(ZoneId.systemDefault()).format(formatter);
			String formattedZoneOffset = product.getPublishTime().atZone(ZoneId.systemDefault()).format(zoneOffsetFormatter);
			publicationInfoView.setPublishTime(String.format("%s (GMT %s)", formattedDateTime, formattedZoneOffset));
		}

		//инфа о продавце
		publicationInfoView.setSeller(getSellerInfo(product));

		//загруженные фотки
		publicationInfoView.setImages(getLoadedImages(product));

		//1 и 2 уровни категории
		Category secondLevelCategory = getSecondLevelCategory(currentCategory);
		publicationInfoView.setChildCategory(secondLevelCategory.getDisplayName());
		publicationInfoView.setChildCategoryId(secondLevelCategory.getId());
		publicationInfoView.setTopCategory(secondLevelCategory.getParent().getDisplayName());
		publicationInfoView.setTopCategoryId(secondLevelCategory.getParent().getId());

		return publicationInfoView;
	}

	@Override
	public PublicationInfoView getSubcategoryOrAttributesForSelectedCategory(Long categoryId) {
		PublicationInfoView publicationInfoView = new PublicationInfoView();

		List<CatalogCategory> directChildrenCategories = categoryService.getDirectChildrenCategories(categoryId);

		//Если выбрана не конечная категория, возвращаем субкатегорию
		if (!directChildrenCategories.isEmpty()) {
			AttributeGroupView attributeGroupView = new AttributeGroupView("subcategory", "Субкатегория");
			directChildrenCategories.forEach(cat ->
					attributeGroupView.add(new AttributeGroupView.Item(
							cat.getId(), cat.getDisplayName(), false, cat.isHasChildren())
					)
			);
			publicationInfoView.setAvalaibleAttributes(Collections.singletonList(attributeGroupView));
		} else {
			// иначе возвращаем возможные атрибуты
			publicationInfoView.setAvalaibleAttributes(getAvalaibleAttributesForCategory(categoryId));
		}
		return publicationInfoView;
	}

	@Override
	public List<PublicationSizeTypeView> getSizeTypesForCategory(Long categoryId) {
		List<CatalogSize> sizesGroupedBySizeType = categoryService.getSizesGroupedBySizeType(categoryId);
		return sizesGroupedBySizeType.stream().map(
				catalogSize -> new PublicationSizeTypeView(
						catalogSize.getSizeType().name(),
						catalogSize.getSizeType().getDescription()
				)
		).collect(Collectors.toList());
	}

	@Override
	public List<PublicationPhotoSample> getPhotoSamplesForCategory(Long category) {

		return photoSampleRepository.findByCategoryId(category,
				new QSort(QPublicationPhotoSample.publicationPhotoSample.photoOrder.asc()));
	}

	/**
	 * На первой странице мы сохраняем конечную категорию, атрибуты и размер
	 * При этом атрибуты должны быть для всех категорий
	 *
	 * @param product товар, для которого мы делаем проверку на состояние публикации
	 * @return мы уже открывали первую страницу
	 */

	private boolean publicationAttributesWasSavedFor(DetailedProduct product) {
		return !product.getAttributeValues().isEmpty();
	}

	/**
	 * Получаем 1-ая, 2-ая, 3-я, 4-ая фотографии публикуемого товара (могут отсутствовать)
	 *
	 * @param product публикуемый товар
	 * @return 1, 2, 3, 4 фотографии
	 */
	private ImagesView getLoadedImages(Product product) {
		ImagesView imagesView = new ImagesView();
		List<ProductImage> productImages = imageService.getProductImages(product);
		Optional<ProductImage> firstImage = productImages.stream().filter(p -> p.getPhotoOrder() == 1).findFirst();
		imagesView.setFirst(firstImage.map(ProductImage::getUrl).orElse(null));

		Optional<ProductImage> secondImage = productImages.stream().filter(p -> p.getPhotoOrder() == 2).findFirst();
		imagesView.setSecond(secondImage.map(ProductImage::getUrl).orElse(null));

		Optional<ProductImage> thirdImage = productImages.stream().filter(p -> p.getPhotoOrder() == 3).findFirst();
		imagesView.setThird(thirdImage.map(ProductImage::getUrl).orElse(null));

		Optional<ProductImage> fourthImage = productImages.stream().filter(p -> p.getPhotoOrder() == 4).findFirst();
		imagesView.setFourth(fourthImage.map(ProductImage::getUrl).orElse(null));
		return imagesView;
	}

	/**
	 * Получить данные о продавце
	 *
	 * @param product публикуемый товар
	 * @return данные о продавце
	 */
	private SellerView getSellerInfo(Product product) {
		SellerRequisite sellerRequisite = product.getSeller().getSellerRequisite();

		SellerView sellerView = new SellerView();
		if (sellerRequisite == null) { return sellerView; }

		sellerView.setAddress(sellerRequisite.getAddress())
				.setCity(sellerRequisite.getCity())
				.setFirstName(sellerRequisite.getFirstName())
				.setLastName(sellerRequisite.getLastName())
				.setPostcode(sellerRequisite.getZipCode())
				.setPhone(sellerRequisite.getPhone());

		return sellerView;
	}

	/**
	 * Получаем категорию 2 уровня того же поддерева, что и указанная категория
	 *
	 * @param currentCategory указанная (текущая) категория
	 * @return категория 2-уровня того же поддерева, что и указанная категория
	 */
	private Category getSecondLevelCategory(Category currentCategory) {
		int i = currentCategory.getCategoryLevel();
		Category category = currentCategory;
		while (i > 2) {
			category = category.getParent();
			i--;
		}
		return category;
	}

	/**
	 * Получить возможные состояния товара с выбранным (если ранее было выбрано состояние товара)
	 *
	 * @param product публикуемый товар
	 * @return возможные состояния товара с выбранным (если ранее было выбрано состояние товара)
	 */
	@Override
	public List<ProductConditionView> getProductConditions(Product product) {
		return publicationService.getAllProductConditions()
				.stream().map(condition -> {
					ProductConditionView productConditionView = new ProductConditionView();
					productConditionView.setId(condition.getId())
							.setName(condition.getName())
							.setDescription(condition.getDescription());
					if(product == null) {
						return productConditionView;
					}
					ProductCondition productCondition = product.getProductCondition();
					if (productCondition != null && productCondition.getId().equals(condition.getId())) {
						productConditionView.setChecked(true);
					}
					return productConditionView;
				}).collect(Collectors.toList());
	}

	/**
	 * Получаем либо доступные для выбора категории, либо доступные для выбора атрибуты
	 *
	 * @param currentCategory текущая категория
	 */
	private PublicationInfoView getNotSavedPublicationInfo(Category currentCategory) {
		PublicationInfoView publicationInfoView = new PublicationInfoView();
		publicationInfoView.setSaved(false);
		//если текущая категория не конечная
		if (!currentCategory.isLeaf()) {
			publicationInfoView.setAvalaibleAttributes(singletonList(getAvalaibleCategories(currentCategory)));
		} else {
			publicationInfoView.setAvalaibleAttributes(getAvalaibleAttributesForCategory(currentCategory.getId()));
			//сразу отображаем контрол с выбором размера
			publicationInfoView.setShowSizeAtOnce(true);
			publicationInfoView.setAvalaibleSizeTypes(getSizeTypesForCategory(currentCategory.getId()));
		}

		return publicationInfoView;
	}

	/**
	 * Получаем:
	 * 1) списки категорий с выбранными ранее категориями
	 * 2) выбранные ранее атрибуты
	 * 3) выбранную ранее размерную сетку
	 * 4) выбранный ранее размер
	 */
	private PublicationInfoView getSavedPublicationInfo(DetailedProduct detailedProduct, Product product, ProductItem productItem,
	                                                    Category currentCategory) {
		PublicationInfoView publicationInfoView = new PublicationInfoView();
		publicationInfoView.setSaved(true);
		int currentCategoryLevel = currentCategory.getCategoryLevel();
		List<AttributeGroupView> avAttrs = new ArrayList<>();
		switch (currentCategoryLevel) {
			case 4:
				avAttrs.add(getSameLevelCategoriesWithChecked(currentCategory.getParent(), 3));
				avAttrs.add(getSameLevelCategoriesWithChecked(currentCategory, 4));
				break;
			case 3:
				avAttrs.add(getSameLevelCategoriesWithChecked(currentCategory, currentCategoryLevel));
				break;
			case 2:
			default:
				break;
		}
		avAttrs.addAll(getCheckedAttributes(detailedProduct));
		publicationInfoView.setAvalaibleAttributes(avAttrs);

		//выбранный тип размерной сетки(может быть null)
		SizeType sizeType = product.getSizeType();
		Size size = productItem.getSize();
		publicationInfoView.setAvalaibleSizeTypes(getSizeTypesForCategory(currentCategory.getId()));

		if (sizeType != null) {
			publicationInfoView.setSizeType(sizeType.name());
		}
		//Если ранее мы установили размер, подгружаем ее, иначе размеры для данного типа размеров отсутствуют
		// и мы сразу отображаем табличку с размерными сетками
		if (size == null) {
			publicationInfoView.setShowSizeAtOnce(true);
		}
		List<PublicationSizeView> avalaibleSizes = getAvalaibleSizes(sizeType, size != null ? size.getId() : null, currentCategory.getId());
		if (!avalaibleSizes.isEmpty()) {
			publicationInfoView.setAvalaibleSizes(avalaibleSizes);
		}
		return publicationInfoView;
	}

	/**
	 * Получить все категории того же уровня и того же родителя, что и категория-параметр , с указанием того,
	 * какая из этих категорий выбрана
	 *
	 * @param currentCategory категория
	 * @return список категорий с выбранной ранее категорией
	 */
	private AttributeGroupView getSameLevelCategoriesWithChecked(Category currentCategory, int currentCategoryLevel) {
		Long parentCategoryId = currentCategory.getParent().getId();
		List<CatalogCategory> directChildrenCategories = categoryService.getDirectChildrenCategories(parentCategoryId);
		AttributeGroupView avalaibleCategories;
		if (currentCategoryLevel == 4) {
			avalaibleCategories = new AttributeGroupView("subcategory", "Субкатегория");
		} else {
			avalaibleCategories = new AttributeGroupView("category", "Категория");
		}
		directChildrenCategories.forEach(currentLevelCategory -> {
			boolean checked = currentLevelCategory.getId().equals(currentCategory.getId());
			avalaibleCategories.add(
					new AttributeGroupView.Item(
							currentLevelCategory.getId(), currentLevelCategory.getDisplayName(), checked,
							currentLevelCategory.isHasChildren()
					));
		});
		return avalaibleCategories;
	}

	/**
	 * Получаем списки атрибутов для товара с указанием на то, какой атрибут выбран
	 */
	private List<AttributeGroupView> getCheckedAttributes(DetailedProduct detailedProduct) {
		return detailedProduct.getAttributeValues().stream().map(attributeValue -> {
			Attribute attribute = attributeValue.getAttribute();
			List<AttributeValue> avalaibleAttributeValues = attribute.getAttributeValues();
			val attributeGroupView = new AttributeGroupView(attribute.getName(), attribute.getName());
			avalaibleAttributeValues.forEach(avalaibleAttributeValue -> {
				boolean checked = avalaibleAttributeValue.getId().equals(attributeValue.getId());
				attributeGroupView.add(new AttributeGroupView.Item(
						avalaibleAttributeValue.getId(), avalaibleAttributeValue.getValue(), checked, false)
				);
			});
			return attributeGroupView;
		}).collect(Collectors.toList());
	}

	/**
	 * Получаем список доступных размеров в выбранной размерной сетке с указанием на то, какой размер был выбран ранее
	 *
	 * @param sizeType          выбранная размерная сетка
	 * @param currentSizeId     выбранный размер товара
	 * @param currentCategoryId текущая категория товара
	 */
	private List<PublicationSizeView> getAvalaibleSizes(SizeType sizeType, Long currentSizeId, long currentCategoryId) {
		return categoryService.getSizesGroupedBySizeType(currentCategoryId) // все возможные размеры для категории, сгруппированные по типу размерной сетки
				.stream()
				.filter(s -> s.getSizeType().equals(sizeType)) // оставляем только выбранную пользователем размерную сетку
				.findFirst()// она должна остаться одна, поэтому берем первую
				.map(CatalogSize::getValues) // получаем список вьюхи (id, название размера в текущей сетке)
				.orElseGet(Collections::emptyList)
				.stream()
				.map(sv -> new PublicationSizeView(sv.getId(), sv.getName(), sv.getId().equals(currentSizeId))) //помечаем выбранный размер
				.collect(Collectors.toList());
	}


	/**
	 * В случае, если мы открыли несохраненную публикацию и текущая категория не является конечной,
	 * подгружаем дочерние категории для текущей
	 *
	 * @param currentCategory текущая категория
	 * @return дочерние категории, доступные для выбора (сгруппированы во вьюху для удобства отображения)
	 */
	private AttributeGroupView getAvalaibleCategories(Category currentCategory) {
		List<CatalogCategory> directChildrenCategories =
				categoryService.getDirectChildrenCategories(currentCategory.getId());
		//формируем их в группу атрибутов
		val avalaibleCategories = new AttributeGroupView("category", "Категория");
		directChildrenCategories.forEach(
				cat -> avalaibleCategories.add(
						new AttributeGroupView.Item(cat.getId(), cat.getDisplayName(), false, cat.isHasChildren())
				));
		return avalaibleCategories;
	}

	/**
	 * В случае, если ранее была выбрана конечная категория (например, на она была выбрана на 1 этапе),
	 * сразу подгружаем список атрибутов
	 *
	 * @param categoryId id текущей категории
	 * @return список доступных для выбора атрибутов, сгруппированных во вьюху для удобства отображения на странице
	 */
	private List<AttributeGroupView> getAvalaibleAttributesForCategory(long categoryId) {
		return categoryService.getAllAttributes(categoryId)
				.stream().map(catalogAttribute -> {
					Attribute attribute = catalogAttribute.getAttribute();
					val attributeGroupView = new AttributeGroupView(attribute.getName(), attribute.getName());
					catalogAttribute.getValues().forEach(
							value -> attributeGroupView.add(
									new AttributeGroupView.Item(value.getId(), value.getValue(), false, false)
							)
					);
					return attributeGroupView;
				}).collect(Collectors.toList());
	}

}
