package su.reddot.domain.service.brand;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.BrandRepository;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.dao.product.ProductAttributeValueBindingRepository;
import su.reddot.domain.dao.product.ProductItemRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.catalog.size.SizeView;
import su.reddot.infrastructure.service.autocomplete.Autocompletable;
import su.reddot.infrastructure.service.autocomplete.AutocompleteMatch;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static su.reddot.domain.model.category.QCategory.category;

@Service
@RequiredArgsConstructor @Slf4j
public class DefaultBrandService implements BrandService, Autocompletable {

	private final BrandRepository       brandRepository;
	private final ProductRepository     productRepository;
	private final CategoryRepository    categoryRepository;
	private final ProductItemRepository itemRepository;
	private final SizeRepository        sizeRepository;

	private final ProductAttributeValueBindingRepository productAttributeValueBindingRepository;
	private final AttributeValueRepository               attributeValueRepository;

	private final ObjectMapper mapper;

	@Override
	public List<AutocompleteMatch> matchAutocompletes(String value) {
		if (value == null || value.trim().isEmpty() || value.trim().length() < 3) {
			return Collections.emptyList();
		}
		String lowerValue = value.toLowerCase();
		List<Brand> brands = brandRepository.findBySubstring(lowerValue);
		return brands.stream().map(b -> new AutocompleteMatch(b.getId(), b.getName())).collect(toList());
	}

	@Override
	public Class getAutocompleteType() {
		return Brand.class;
	}

	@Override
	public Optional<Brand> findByName(String name) {
		return Optional.ofNullable(brandRepository.findFirstByName(name));
	}

	@Override
	public Optional<Brand> findById(Long id) {
		return Optional.ofNullable(brandRepository.getOne(id));
	}

	@Override
	public HashMap<String, List<Brand>> getBrandsInGroups() {
		HashMap<String, List<Brand>> result = new HashMap<>();
		List<String> chars = brandRepository.getDistinctCharsOfBrands();
		for(String firstChar : chars){
			result.put(firstChar, brandRepository.findAllByFirstChar(firstChar));
		}
		return result;
	}

    @Override
    public List<Category> getActualCategories(Brand brand, Long nullableParentCategoryId) {


		List<Category> actualCategories = productRepository.getActualCategories(brand);
		if (actualCategories.isEmpty()) return Collections.emptyList();

		BooleanBuilder predicate = new BooleanBuilder();
		for (Category actualCategory : actualCategories) {
			predicate.or(category.leftOrder.lt(actualCategory.getLeftOrder())
					.and(category.rightOrder.gt(actualCategory.getRightOrder())));
		}

		predicate.and(nullableParentCategoryId == null?
				category.parent.leftOrder.eq(1)
				: category.parent.id.eq(nullableParentCategoryId));

		Iterable<Category> found = categoryRepository.findAll(predicate);
		List<Category> cooked = new ArrayList<>();
		found.forEach(cooked::add);

		return cooked;
	}

	@Override
	public List<ProductCondition> getActualConditions(Brand brand, Long category) {
		Category c = category == null? null : categoryRepository.findOne(category);

		return productRepository.getActualConditions(brand, c);
	}

	@Override
	public Map<Attribute, List<AttributeValue>> getActualAttributeValues(Brand brand, Long categoryId) {

		Category c = categoryId == null? null : categoryRepository.findOne(categoryId);

		/* Сейчас пока нельзя вычислить объединение всех возможных атрибутов всех товаров данного бренда,
		* нужно обязательно указывать конкретную категорию, в которой идет поиск товаров. */
		if (c == null) { return Collections.emptyMap(); }

		List<Long> actualAttributeValuesIds
				= productAttributeValueBindingRepository.getActualAttributeValues(c, brand);
		List<AttributeValue> actualAttributeValues = attributeValueRepository.findAll(actualAttributeValuesIds);

		return actualAttributeValues.stream()
				.collect(Collectors.groupingBy(AttributeValue::getAttribute));
	}

	@Override
	public List<CatalogSize> getActualSizes(Brand brand, Long category) {
		Category interestingCategory = categoryRepository.findOne(category);
		if (interestingCategory == null) { return Collections.emptyList(); }

		List<Long> actualSizesIds = itemRepository.getActualSizes(brand, interestingCategory);
		List<Size> actualSizes = sizeRepository.findAll(actualSizesIds);

		return cookSizes(actualSizes);
	}

	@Override
	public boolean getVintageProductsPresence(Brand brand, Long categoryId) {
		Category c = categoryId == null? null : categoryRepository.findOne(categoryId);

		return c == null? productRepository.getVintageProductsPresence(brand)
				: productRepository.getVintageProductsPresence(brand, c);
	}

	@Override
	public boolean getNewCollectionProductsPresence(Brand brand, Long categoryId) {
		Category c = categoryId == null? null : categoryRepository.findOne(categoryId);

		return c == null? productRepository.getNewCollectionProductsPresence(brand)
				: productRepository.getNewCollectionProductsPresence(brand, c);
	}

	@Override
	public boolean getProductsWithOurChoicePresence(Brand brand, Long categoryId) {
		Category c = categoryId == null? null : categoryRepository.findOne(categoryId);

		return c == null? productRepository.getProductsWithOurChoicePresence(brand)
				: productRepository.getProductsWithOurChoicePresence(brand, c);
	}

	@Override
	public boolean getSaleProductsPresence(Brand brand, Long categoryId) {
		Category c = categoryId == null? null : categoryRepository.findOne(categoryId);

		return c == null? productRepository.getSaleProductsPresence(brand)
				: productRepository.getSaleProductsPresence(brand, c);
	}

	@Override
	public Optional<Brand> getByUrl(String url){ return brandRepository.findFirstByUrl(url); }

	@Override
	public List<Brand> getAll() {
		return brandRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
	}

	private List<CatalogSize> cookSizes(List<Size> sizes) {
		List<CatalogSize> cookedSizes = new ArrayList<>();

		for (SizeType sizeType : SizeType.values()) {
			List<SizeView> sizesOfGivenType = new ArrayList<>();

			for (Size size : sizes) {
				String sizebySizeType = size.getBySizeType(sizeType);

				if (sizebySizeType != null) {
					try {
						SizeView sizeView = new SizeView(size.getId(), size.getBySizeType(sizeType),
								mapper.writeValueAsString(size.getChart()));
						sizesOfGivenType.add(sizeView);
					} catch (JsonProcessingException e) {
						log.error(e.getMessage(), e);
					}
				}
			}

			if (!sizesOfGivenType.isEmpty()) {
				cookedSizes.add(new CatalogSize(sizeType, sizesOfGivenType));
			}
		}

		return cookedSizes;
	}

}
