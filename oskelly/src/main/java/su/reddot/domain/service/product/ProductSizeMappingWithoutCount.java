package su.reddot.domain.service.product;

import lombok.Value;
import su.reddot.domain.model.size.Size;

import java.math.BigDecimal;

/**
 * Использовать внимательно. В одних случаях в поле price
 * подставляется цена с комиссией, в других - без
 * @author Vitaliy Khludeev on 08.07.17.
 */
@Value
public class ProductSizeMappingWithoutCount {

	private final Size size;
	private final BigDecimal price;

	public boolean equals(Object o) {
		if (this == o) {return true;}
		if (o == null || getClass() != o.getClass()) {return false;}

		ProductSizeMappingWithoutCount that = (ProductSizeMappingWithoutCount) o;

		if((size == null && that.size != null) || (size != null && that.size == null)) {
			return false;
		}
		if (size != null && !size.getId().equals(that.size.getId())) {return false;}

		if((price == null && that.price != null) || (price != null && that.price == null)) {
			return false;
		}
		return price == null || price.stripTrailingZeros().equals(that.price.stripTrailingZeros());
	}

	@Override
	/*
	  TODO: нужен тест
	 */
	public int hashCode() {
		int result = 47;
		result = 31 * result + (size != null ? size.getId().hashCode() : 0);
		result = 31 * result + (price != null ? price.stripTrailingZeros().hashCode() : 0);
		return result;
	}
}
