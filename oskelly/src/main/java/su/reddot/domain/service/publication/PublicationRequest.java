package su.reddot.domain.service.publication;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PublicationRequest {
	@NotNull(message = "Не выбрана категория товара")
	@Min(value = 2, message = "Не выбрана категория товара")
	private Long topCategory;
	@NotNull(message = "Не выбран тип товара")
	@Min(value = 2, message = "Не выбран тип товара")
	private Long childCategory;
	@NotNull
	private Long brand;
}
