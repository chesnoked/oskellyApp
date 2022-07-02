package su.reddot.domain.service.admin.moderation;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import su.reddot.domain.service.product.ProductSizeMapping;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 04.08.17.
 */
@Getter
@Setter
public class ProductSizeMappingBaseRequest {

	/**
	 * Можно указывать ни одной позиции или нет
	 */
	private final boolean allowNonePositions;

	@NotEmpty(message = "Не создан ни один экземпляр товара")
	private List<ProductSizeMapping> productSizeMappings;

	public ProductSizeMappingBaseRequest(boolean allowNonePositions) {
		this.allowNonePositions = allowNonePositions;
	}
}
