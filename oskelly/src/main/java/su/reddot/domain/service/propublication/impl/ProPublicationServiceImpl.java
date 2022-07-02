package su.reddot.domain.service.propublication.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.dao.product.ProductAttributeValueBindingRepository;
import su.reddot.domain.dao.product.ProductConditionRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.*;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogAttribute;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.product.ProductSizeMapping;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.propublication.ProPublicationService;
import su.reddot.domain.service.propublication.exception.ProPublicationException;
import su.reddot.domain.service.propublication.view.ProPublicationGridInfo;
import su.reddot.domain.service.propublication.view.ProPublicationRequest;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.util.ErrorNotification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static su.reddot.domain.model.product.ProductItem.State.INITIAL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProPublicationServiceImpl implements ProPublicationService {

	private final BrandService               brandService;
	private final CategoryService            categoryService;
	private final CategoryRepository         categoryRepository;
	private final SizeRepository             sizeRepository;
	private final AttributeValueRepository   attributeValueRepository;
	private final UserService                userService;
	private final ProductConditionRepository productConditionRepository;

	private final ProductAttributeValueBindingRepository productAttributeValueBindingRepository;
	private final ProductRepository productRepository;
	private final ProductItemService productItemService;

	@Override
	@Transactional
	public Product publishProduct(ProPublicationRequest request,
	                              Long sellerId,
	                              ErrorNotification errorNotification) throws ProPublicationException {
		Optional<User> user = userService.getUserById(sellerId);
		if (!user.isPresent() || user.get().getProStatusTime() == null) {
			throw new ProPublicationException("Данный пользователь не имеет доступа к созданию товара");
		}

		Optional<Brand> brand = brandService.findById(request.getBrand());
		if (!brand.isPresent()) {
			errorNotification.add("brand", "Некорректное значение");
			return null;
		}

		Category category = categoryRepository.findOne(request.getCategory());
		if (!category.isLeaf()) {
			errorNotification.add("category", "Некорректное значение");
			return null;
		}

		SizeType sizeType;
		try {
			sizeType = SizeType.valueOf(request.getSizeType());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			errorNotification.add("sizeType", "Некорректное значение");
			return null;
		}

		List<Long> attributeValueIds = request.getAttributeValues();
		if(attributeValueIds.stream().anyMatch(Objects::isNull)) {
			errorNotification.add("attributeValues", "Не указан один из атрибутов");
			return null;
		}
		List<AttributeValue> attributeValues = attributeValueIds.stream()
				.map(attributeValueRepository::findOne).collect(toList());

		ProductCondition productCondition = productConditionRepository.findAll()
				.stream().filter(c -> c.getName().equalsIgnoreCase("С биркой")).findFirst().orElse(null);

		Product product = new Product();
		product.setBrand(brand.get());
		product.setCategory(category);
		product.setDescription(request.getDescription());
		product.setSizeType(sizeType);
		product.setVendorCode(request.getArticle());
		product.setSeller(user.get());
		product.setProductCondition(productCondition);
		product.setRrpPrice(request.getRrpPrice());
		product.setVintage(request.getVintage());
		product.setNewCollection(request.getNewCollection());
		product.setProductState(ProductState.NEED_MODERATION);

		List<ProductAttributeValueBinding> productAttributeValueBindings = new ArrayList<>();
		for (AttributeValue attributeValue : attributeValues) {
			ProductAttributeValueBinding binding = new ProductAttributeValueBinding();
			binding.setAttributeValue(attributeValue);
			binding.setProduct(product);
			productAttributeValueBindings.add(binding);
		}


		List<ProductSizeMapping> items = request.getProductSizeMappings();
		if (items.isEmpty()) {
			errorNotification.add("items", "Не создан ни один товар");
			return null;
		}

		List<ProductItem> productItems = new ArrayList<>();
		for (ProductSizeMapping item : items) {
			Size size = sizeRepository.findOne(item.getSize().getId());
			BigDecimal price = item.getPrice();
			Long count = item.getCount();
			for (int i = 0; i < count; i++) {
				ProductItem productItem = new ProductItem();
				productItem.setProduct(product);
				productItem.setSize(size);
				productItem.setCurrentPriceWithoutCommission(price);
				productItem.setStartPrice(price);
				productItem.setState(INITIAL);
				productItems.add(productItem);
			}
		}

		productRepository.save(product);
		productAttributeValueBindingRepository.save(productAttributeValueBindings);
		try {
			productItemService.save(productItems);
		} catch (CommissionException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new ProPublicationException("Ошибка при расчете комиссии", e);
		}

		return product;
	}

	@Override
	public ProPublicationGridInfo getPublicationGridInfo(Long categoryId) {
		final List<CatalogCategory> leafCategories = categoryService.getLeafCategories(categoryId);
		final List<CatalogAttribute> allAttributes = categoryService.getAllAttributes(categoryId);
		final List<SizeType> sizeTypes = categoryService.getSizesGroupedBySizeType(categoryId)
				.stream().map(CatalogSize::getSizeType).collect(toList());
		ProPublicationGridInfo proPublicationGridInfo = new ProPublicationGridInfo();
		proPublicationGridInfo.setCategories(
				leafCategories.stream().map(c -> {
					Category category = categoryRepository.findOne(c.getId());
					if(category.getParent() == null) {
						return c;
					}
					return new CatalogCategory(c.getId(), c.getDisplayName() + " (" + category.getParent().getDisplayName() + ")", c.isHasChildren());
				}).collect(toList())
		);
		proPublicationGridInfo.setAttributes(
				allAttributes.stream().map(ProPublicationGridInfo.ProPublicationAttribute::new).collect(toList())
		);
		proPublicationGridInfo.setSizeTypes(
				sizeTypes.stream().map(ProPublicationGridInfo.ProPublicationSizeType::new).collect(toList())
		);
		return proPublicationGridInfo;
	}
}
