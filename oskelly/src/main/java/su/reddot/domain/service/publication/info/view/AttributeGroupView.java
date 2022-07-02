package su.reddot.domain.service.publication.info.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Группировка элементов на странице публикации в блоке информации(как атрибуты, так и категории)
 */
@Data
public class AttributeGroupView {

	private String name;
	private String displayName;
	private List<Item> items;

	public AttributeGroupView(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public void add(Item item) {
		if (items == null) {
			items = new ArrayList<>();
		}
		items.add(item);
	}

	/**
	 * Используется, в том числе, для категорий
	 */
	@Data
	@AllArgsConstructor
	public static class Item {
		private Long value;
		private String title;
		private boolean checked;

		//костыль - применим только для категорий
		private boolean hasChildren;
	}
}
