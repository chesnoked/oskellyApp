package su.reddot.presentation.view.product;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Элементы, отображаемые во вкладке "Описание" на странице товара
 */
@Data
@AllArgsConstructor
public class DescriptionAttributeView {
	private String title;
	private String value;
}
