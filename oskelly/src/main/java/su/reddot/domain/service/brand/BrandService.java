package su.reddot.domain.service.brand;

import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.service.catalog.size.CatalogSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BrandService {
    /**
     * @return список брендов, упорядоченных в алфавитном порядке по возрастанию
     */
    List<Brand> getAll();

	Optional<Brand> findByName(String name);

	Optional<Brand> findById(Long id);

	Optional<Brand> getByUrl(String url);

    /**
     *
     * @return Бренды, сгруппированные по первой букве
     */
	HashMap<String, List<Brand>> getBrandsInGroups();

	/** Список категорий, в которых есть товары данного бренда.
	 *
	 * @param nullableParentCategoryId
	 * если задан, то выбрать среди найденных категорий только те,
	 * у которых родительская категория совпадает с переданной
	 */

    List<Category> getActualCategories(Brand brand, Long nullableParentCategoryId);

	List<ProductCondition> getActualConditions(Brand brand, Long category);

	Map<Attribute, List<AttributeValue>> getActualAttributeValues(Brand b, Long categoryId);

	/**
	 * @return только те значения размеров, которые присущи товарам в указанной категории.
	 */
	List<CatalogSize> getActualSizes(Brand brand, Long category);

    boolean getVintageProductsPresence(Brand brand, Long category);

	boolean getNewCollectionProductsPresence(Brand brand, Long category);

	boolean getProductsWithOurChoicePresence(Brand brand, Long category);

	boolean getSaleProductsPresence(Brand brand, Long category);
}
