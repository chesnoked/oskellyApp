package su.reddot.domain.service.admin.moderation;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.BrandRepository;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.product.*;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 22.06.17.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultModerationService implements ModerationService {

	private final ProductService     productService;
	private final CategoryService    categoryService;
	private final ProductItemService productItemService;
	private final PublicationService publicationService;

	private final ProductRepository productRepository;
	private final BrandRepository brandRepository;
	private final SizeRepository sizeRepository;

	@Override
	public CatalogProductPage getCatalogProductPage(int pageNumber) {
		return getCatalogProductPage(pageNumber, null, null, null);
	}

	@Override
	public CatalogProductPage getCatalogProductPage(int pageNumber, Long sellerId) {
		return getCatalogProductPage(pageNumber, null, null, null, sellerId);
	}

	@Override
	public CatalogProductPage getCatalogProductPage(int pageNumber, ProductState productState,
													Boolean isPro, Boolean isVip) {
		return getCatalogProductPage(pageNumber, productState, isPro, isVip, null);
	}

	@Override
	public CatalogProductPage getCatalogProductPage(int pageNumber, ProductState productState, Boolean isPro, Boolean isVip, Long sellerId) {
		ProductService.FilterSpecification filterSpecification = new ProductService.FilterSpecification()
				.state(productState)
				.isVip(isVip)
				.isPro(isPro)
				.sellerId(sellerId);
		ProductService.SortAttribute sortAttribute = ProductService.SortAttribute.ID;
		return getCatalogProductPage(filterSpecification, pageNumber, sortAttribute);
	}

	@Override
	public CatalogProductPage getCatalogProductPage(ProductService.FilterSpecification filterSpecification, int pageNumber, ProductService.SortAttribute sortAttribute) {
		filterSpecification.itemState(ProductItem.State.INITIAL);
		CatalogProductPage catalogProductPage = productService.getProducts(
				filterSpecification,
				pageNumber,
				sortAttribute);

		catalogProductPage.getProducts().forEach(p -> {
			Product product = p.getProduct();
			String fullPath = getFullCategoryPath(product);
			p.setFullPath(fullPath);
			Set<ProductSizeMapping> roundedProductSizeMappings = getRoundedProductSizeMappings(p.getProduct());
			p.setProductSizeMappings(roundedProductSizeMappings);
		});
		return catalogProductPage;
	}

	private Set<ProductSizeMapping> getRoundedProductSizeMappings(Product product) {
		Set<ProductSizeMapping> productSizeMappings = productItemService.getProductSizeMappings(product);
		return productSizeMappings.stream()
				.map(m ->
						new ProductSizeMapping(
								m.getSize(),
								m.getCount(),
								m.getPrice() != null ? m.getPrice().setScale(0, BigDecimal.ROUND_DOWN) : null,
								m.getPriceWithCommission() != null ? m.getPriceWithCommission().setScale(0, BigDecimal.ROUND_UP) : null
						)
				)
				.collect(Collectors.toSet());
	}

	@Override
	public Optional<DetailedProduct> getDetailedProduct(Long productId) {
		Optional<DetailedProduct> detailedProduct = productService.getProduct(productId);
		detailedProduct.ifPresent(d -> {
			d.setFullPath(getFullCategoryPath(d.getProduct()));
			d.setProductSizeMappings(getRoundedProductSizeMappings(d.getProduct()));
		});
		return detailedProduct;
	}

	@Override
	@Transactional
	public void updateAttributesAndCategory(Product product, Long categoryId, List<Long> attributeValues) throws PublicationException {
		publicationService.updateCategory(product, categoryId);
		publicationService.updateAttributes(product, attributeValues);
	}

	@Override
	public void updateMarks(Long productId, Boolean vintage, Boolean ourChoice, boolean isNewCollection) {
		Product product = productRepository.findOne(productId);
		product.setVintage(vintage);
		saveNewCollection(isNewCollection, product);
		if(ourChoice && product.getOurChoiceStatusTime() == null) {
			product.setOurChoiceStatusTime(LocalDateTime.now());
		}
		else if(!ourChoice) {
			product.setOurChoiceStatusTime(null);
		}
		productRepository.save(product);
	}

	@Override
	public void updateMarks(Long productId, Boolean vintage, boolean isNewCollection) {
		Product product = productRepository.findOne(productId);
		product.setVintage(vintage);
		saveNewCollection(isNewCollection, product);
		productRepository.save(product);
	}

	private void saveNewCollection(boolean isNewCollection, Product product) {
		product.setNewCollection(isNewCollection);
		List<ProductItem> productItems = productItemService.getItemsByStateAndProduct(ProductItem.State.INITIAL, product);
		try {
			productItemService.save(productItems);
		} catch (CommissionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void updateBrand(Long productId, Long brandId) {
		Product product = productRepository.findOne(productId);
		Brand brand = brandRepository.findOne(brandId);
		product.setBrand(brand);
		productRepository.save(product);
	}

	@Override
	public void updateRrpPrice(Long productId, BigDecimal rrp) {
		Product product = productRepository.findOne(productId);
		product.setRrpPrice(rrp);
		productRepository.save(product);
	}

	@Override
	public void updateVendorCode(Long productId, String vendorCode) {
		Product product = productRepository.findOne(productId);
		product.setVendorCode(vendorCode);
		productRepository.save(product);
	}

	@Override
	public void updateModel(Long productId, String model) {
		Product product = productRepository.findOne(productId);
		product.setModel(model);
		productRepository.save(product);
	}

	@Override
	@Transactional
	public void updateProductItemsCount(Long productId, List<ProductSizeMapping> productSizeMappings) throws CommissionException {
		Product product = productRepository.findOne(productId);
		if(!product.getSeller().isPro()) {
			throw new IllegalArgumentException("Товар принадлежит пользователю без статуса PRO");
		}
		Set<ProductSizeMapping> cookedProductSizeMappings = productSizeMappings.stream()
				.map(m -> {
					// размер выдергиваем из базы, для корретного сохрания позиций товаров
					Size size = sizeRepository.findOne(m.getSize().getId());
					if(size == null) {
						throw new NullPointerException("Size cannot be null");
					}
					return new ProductSizeMapping(size, m.getCount(), m.getPrice().stripTrailingZeros());
				})
				.collect(Collectors.toSet());
		updateProductSizeMappings(product, productItemService.getProductSizeMappings(product), cookedProductSizeMappings);
	}

	private String getFullCategoryPath(Product product) {
		String fullPath = categoryService.getAllParentCategories(product.getCategory().getId()).stream()
				.map(CatalogCategory::getDisplayName)
				.collect(Collectors.joining(" > "));
		fullPath = String.join(" > ", fullPath, product.getCategory().getDisplayName());
		return fullPath;
	}

	/**
	 * TODO: Написать тест с использованием БД
	 */
	private void updateProductSizeMappings(Product product, Set<ProductSizeMapping> oldMappings, Set<ProductSizeMapping> newMappings) throws CommissionException {
		createAndUpdateMappingPositions(product, oldMappings, newMappings);
		deleteMappingPositions(product, oldMappings, newMappings);
	}

	/**
	 * Логика такая. Все что обработано в методе createAndUpdateMappingPositions сюда попасть не должно.
	 * То есть, сюда доджны попасть строки, которые есть в старом массиве, но нет в новом массиве.
	 * Проверка на то есть строки или нет должна идти по размеру и цене, исключая при этом количество
	 * @param product товар
	 * @param oldMappings старые значения
	 * @param newMappings новые значения
	 * @see ProductSizeMappingWithoutCount#equals(Object)
	 * @see ProductSizeMappingWithoutCount#hashCode()
	 */
	private void deleteMappingPositions(Product product, Set<ProductSizeMapping> oldMappings, Set<ProductSizeMapping> newMappings) {
		Set<ProductSizeMappingWithoutCount> newMappingsWithoutCount = newMappings.stream()
				.map(m -> new ProductSizeMappingWithoutCount(m.getSize(), m.getPrice()))
				.collect(Collectors.toSet());
		Set<ProductSizeMapping> diffForDelete = Sets.difference(oldMappings, newMappings);
		diffForDelete.forEach(diffMapping -> {
			if(!newMappingsWithoutCount.contains(new ProductSizeMappingWithoutCount(diffMapping.getSize(), diffMapping.getPrice()))) {
				productItemService.deleteByProductAndSizeAndPrice(product, diffMapping.getSize(), diffMapping.getPrice(), diffMapping.getCount().intValue());
			}
		});
	}

	/**
	 * Логика такая. Сюда доджны попасть строки, которые есть в новом массиве, но нет в старом массиве.
	 * Также сюда попадаю строки, которые есть и там и там, но отличаются по цене
	 * @param product товар
	 * @param oldMappings старые значения
	 * @param newMappings новые значения
	 * @see ProductSizeMapping#equals(Object)
	 * @see ProductSizeMapping#hashCode()
	 */
	private void createAndUpdateMappingPositions(Product product, Set<ProductSizeMapping> oldMappings, Set<ProductSizeMapping> newMappings) throws CommissionException {
		ProductItem firstProductItem = productItemService.findFirstByProduct(product);
		Set<ProductSizeMapping> diffForCreateAndUpdate = Sets.difference(newMappings, oldMappings);
		for (ProductSizeMapping diffMapping : diffForCreateAndUpdate) {
			// Если товар опубликован, то его startPrice надо взять из базы
			// Если товар не опубликован, то его startPrice будет такой же как текущи currentPrice
			BigDecimal stPrice = (product.getProductState() == ProductState.PUBLISHED ?
					productItemService.getMaxStartPriceByProduct(product)
					: diffMapping.getPrice());
			// TODO: получать currentCount из соответсвующего элемента oldMappings, чтобы не делать лишний запрос к БД. Учесть, что записи может просто не быть, в этом случае значение должно быть равно 0
			Long currentCount = productItemService.countByProductAndSizeAndPrice(product, diffMapping.getSize(), diffMapping.getPrice());
			Integer delta = diffMapping.getCount().intValue() - currentCount.intValue();
			if(currentCount == 0L && diffMapping.getCount() == 0L) { // в данной ситуации ничего не делаем, она вознкает когда пользователь создал строку с количеством 0, а в базе и так ничего не было по этой строке
				return;
			}
			if(delta == 0) { // кидаем эксепшин, т.к. в diffForCreateAndUpdate такая запись попасть не должна
				throw new RuntimeException("Delta cannot be 0"); // TODO: свой эксепшн
			}
			else if(delta > 0) {
				for (int i = 0; i < Math.abs(delta); i++) {
					ProductItem productItem = new ProductItem();
					productItem.setProduct(firstProductItem.getProduct());
					productItem.setSerialNumber(firstProductItem.getSerialNumber());
					productItem.setSize(diffMapping.getSize());
					productItem.setStartPrice(stPrice);
					productItem.setCurrentPriceWithoutCommission(diffMapping.getPrice());
					productItem.setState(ProductItem.State.INITIAL);
					productItemService.save(productItem);
				}
			}
			else {
				productItemService.deleteByProductAndSizeAndPrice(product, diffMapping.getSize(), diffMapping.getPrice(), Math.abs(delta));
			}
		}
	}
}
