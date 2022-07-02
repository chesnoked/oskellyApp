package su.reddot.domain.service.propublication.view;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import su.reddot.domain.service.admin.moderation.ProductSizeMappingBaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProPublicationRequest extends ProductSizeMappingBaseRequest {

	@NotEmpty(message = "Не указан артикул товара")
	private String article;

	@NotNull(message = "Не указана категория товара")
	@Min(value = 1, message = "Не указана категория товара")
	private Long category;

	@NotNull(message = "Не указан бренд товара")
	@Min(value = 1, message = "Не указан бренд товара")
	private Long brand;

	@NotEmpty(message = "Не выбраны атрибуты товара")
	private List<Long> attributeValues;

	private String description;

	@NotEmpty(message = "Не выбрана размерная сетка")
	private String sizeType;

	private BigDecimal rrpPrice;

	private Boolean vintage;

	private Boolean newCollection;

	public ProPublicationRequest() {
		super(false);
	}
}
