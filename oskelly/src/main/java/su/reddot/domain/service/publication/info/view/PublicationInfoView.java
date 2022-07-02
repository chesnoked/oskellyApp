package su.reddot.domain.service.publication.info.view;

import lombok.Data;
import lombok.experimental.Accessors;
import su.reddot.domain.model.size.SizeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * @see su.reddot.presentation.mobile.api.v1.PublicationRestControllerV1
 */
@Accessors(chain = true)
@Data
public class PublicationInfoView {

	private boolean saved;
	private Long id;
	private String brand;
	private boolean vintage;
	private String description;
	//категорий 2 уровня
	private String childCategory;
	private Long childCategoryId;
	//категория 1 уровня
	private String topCategory;
	private Long topCategoryId;
	private BigDecimal price;
	private BigDecimal priceWithCommission;
	private List<AttributeGroupView> avalaibleAttributes;
	private List<ProductConditionView> productConditions;

	private String model;
	private String origin;
	private BigDecimal purchasePrice;
	private Integer purchaseYear;
	private String serialNumber;

	private SellerView seller;

	private List<PublicationSizeTypeView> avalaibleSizeTypes;
	private String sizeType;
	private List<PublicationSizeView> avalaibleSizes;
	/**
	 * Показывать размеры сразу. Т.е.если категория 2-го уровня, то показывать размеры сразу,
	 * не дожидаясь выбора категорий более низкого уровня
	 */
	private boolean showSizeAtOnce = false;

	private ImagesView images;

	private Boolean ourChoice;

	private boolean newCollection;

	private String publishTime;

	private BigDecimal rrpPrice;

	private String vendorCode;
}
