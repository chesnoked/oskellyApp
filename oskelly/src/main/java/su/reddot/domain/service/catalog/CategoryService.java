package su.reddot.domain.service.catalog;

import su.reddot.domain.model.category.Category;
import su.reddot.domain.service.catalog.size.CatalogSize;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    /**
     * Получить всю структуру каталога. Каталог может содержать в себе
     * несколько корневых категорий.
     *
     * @return список корневых категорий, каждая из которых содержит
     * информацию о дочерних категориях.
     */
    List<CatalogCategory> getEntireCatalog();

    /**
     * Получить данные о прямых потомках данной категории, если такие есть.
     * @param parentCategoryId идентификатор категории - родителя.
     * @return список прямых потомков данной категории.
     */
    List<CatalogCategory> getDirectChildrenCategories(Long parentCategoryId);

	/**
	 * Как {@link #getDirectChildrenCategories(Long)}, только возвращает категории, у которых есть доступные товары.
	 */
	List<CatalogCategory> getDirectChildrenCategories(Long parentCategoryId, boolean nonEmptyOnly);

    /**
     * Получить список всех родительских категорий, начиная с самой старшей.
     * @param categoryId идентификатор дочерней категории
     * @return список всех родительских категорий.
     */
    List<CatalogCategory> getAllParentCategories(Long categoryId);

    /**
     * Получить список идентификаторов всех категорий - "листьев" для данной
     * родительской категории. Если категория сама является листом, вернуть только ее.
     * @param parentId идентификатор родительской категории
     * @return список идентификаторов дочерних категорий - "листьев"
     */
    List<Long> getLeafCategoriesIds(Long parentId);

	/**
	 * Получить список всех дочерних категорий - "листьев" для данной родительской категории
	 *
	 * @param parentId идентификатор родительской категории
	 * @return список дочерних категорий - "листьев"
	 */
	List<CatalogCategory> getLeafCategories(Long parentId);

    Optional<CatalogCategory> findById(Long id);
	Optional<CatalogCategory> findByUrl(String url);

	/**
	 * Получить для данной категории список всех атрибутов и их значения.
	 * В список атрибутов входят как непосредственные атрибуты данной категории,
	 * так и те, что наследуются от родительских категорий.
	 *
	 * @param categoryId идентификатор категории
	 * @return список атрибутов и их возможные значения
	 */
	List<CatalogAttribute> getAllAttributes(Long categoryId);

	/**
	 * Получить размеры для указанной категории.
	 * Если у категории нет собственных размеров,
	 * использовать размеры <b>ближайшей</b> родительской категории,
	 * у которой эти размеры есть.
	 *
	 * @param categoryId идентификатор категории
	 * @return размеры товаров, сгруппированные по системам отображения размеров
	 */
	List<CatalogSize> getSizesGroupedBySizeType(Long categoryId);

	Optional<Category> findOne(Long id);

	/**
     * Создать новую дочернюю категорию для данной категории - родителя {@code parent}.
	 * Новая категория добавляется в начало списка других детей {@code parent}, если таковые есть.
	 * @param parentId <b>not null</b> категория - родитель, к которой добавляется создаваемая категория.
	 * @param displayName <b>not null</b> название категории
	 * @param urlName <b>not null</b> как категория будет отображаться в адресной строке
	 * @param singularName <b>nullable</b> название категории как оно будет отображаться в названии товара.
	 *                     Как правило название задается в единственном числе именительно падеже.
	 *                     Если не указано, в названии товара будет использоваться значение {@code displayName}
	 * @return созданная категория
	 */
	Category create(Long parentId, String displayName, String urlName, String singularName);

	void temporaryInit();
}
