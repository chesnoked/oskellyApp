package su.reddot.domain.service.catalog;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.presentation.mobile.api.v1.CatalogRestControllerV1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Представление сущности {@link su.reddot.domain.model.category.Category},
 * которое не содержит лишних для клиента данных.
 * Эти данные представляют собой детали реализации иерарахии категорий с
 * помощью метода вложенных множеств (nested sets): левый, правый порядки,
 * ссылка на родительскую категорию.
 *
 * ВНИМАНИЕ!!! Править осторожно! Используется в API MOBILE V1
 * Можно сломать мобильное приложение
 * @see CatalogRestControllerV1#getTree() 
 */
@Getter @Setter @Accessors(chain = true)
public class CatalogCategory {

	private Long id;

	private String urlName;
	private String displayName;
	private String fullName;

	private List<CatalogCategory> children = new ArrayList<>();

	private boolean hasChildren;

	/** У категории нет товаров. У родительской категории нет товаров, если
	 * их нет ни у одной из ее дочерних категорий - листьев. */
	private boolean isEmpty;

	/* для совместимости с имеющимся кодом */
	public CatalogCategory(Long id, String displayName, boolean hasChildren) {
	    this(id, displayName, hasChildren, false);
	}

	public CatalogCategory(Long id, String displayName, boolean hasChildren, boolean isEmpty) {
		this.id = id;
		this.displayName = displayName;
		this.hasChildren = hasChildren;
		this.isEmpty = isEmpty;
	}

	public List<CatalogCategory> getSortedChildren() {

		List<CatalogCategory> sortedChildren = new ArrayList<>(children);
		sortedChildren.sort(Comparator.comparing(CatalogCategory::getDisplayName));

		return sortedChildren;
	}

	/** FIXME временное решение для вывода подкатегорий раздела женских товаров в нужном порядке.
	 * Правильное решение: создать программный метод переставления элементов дерева категорий */
	public List<CatalogCategory> getWomenProductsCategoryChildren() {

		boolean categoryIsWomenProductsCategory = "Женское".equalsIgnoreCase(displayName);
		if (!categoryIsWomenProductsCategory) { return children; }

		return Arrays.asList(
				children.stream().filter(c -> "Одежда".equalsIgnoreCase(c.getDisplayName())).findFirst().orElseThrow(RuntimeException::new),
				children.stream().filter(c -> "Обувь".equalsIgnoreCase(c.getDisplayName())).findFirst().orElseThrow(RuntimeException::new),
				children.stream().filter(c -> "Сумки".equalsIgnoreCase(c.getDisplayName())).findFirst().orElseThrow(RuntimeException::new),
				children.stream().filter(c -> "Аксессуары".equalsIgnoreCase(c.getDisplayName())).findFirst().orElseThrow(RuntimeException::new));
	}
}
