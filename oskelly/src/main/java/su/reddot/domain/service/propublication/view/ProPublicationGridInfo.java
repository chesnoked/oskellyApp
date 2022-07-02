package su.reddot.domain.service.propublication.view;

import lombok.Data;
import lombok.Getter;
import lombok.Value;
import su.reddot.domain.model.attribute.SimpleAttributeValue;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.CatalogAttribute;
import su.reddot.domain.service.catalog.CatalogCategory;

import java.util.List;

/**
 * Данные для формирования формы добавления товара в pro-публикации
 */
@Data
public class ProPublicationGridInfo {

	private List<CatalogCategory> categories;

	private List<ProPublicationAttribute> attributes;

	private List<ProPublicationSizeType> sizeTypes;

	@Getter
	public static class ProPublicationAttribute {
		private String name;
		private List<SimpleAttributeValue> values;

		public ProPublicationAttribute(CatalogAttribute catalogAttribute) {
			this.name = catalogAttribute.getAttribute().getName();
			this.values = catalogAttribute.getValues();
		}
	}

	@Getter
	public static class ProPublicationSizeType {
		private String name;
		private String displayName;

		public ProPublicationSizeType(SizeType sizeType) {
			this.name = sizeType.name();
			this.displayName = sizeType.getDescription();
		}
	}
}
