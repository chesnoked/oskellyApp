package su.reddot.domain.service.admin.moderation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Vitaliy Khludeev on 07.07.17.
 */
@Getter
@Setter
public class ProductSizeMappingRequest extends ProductSizeMappingBaseRequest {

	@NotNull(message = "Не указан идентификатор товара")
	private Long productId;

	public ProductSizeMappingRequest() {
		super(true);
	}
}
