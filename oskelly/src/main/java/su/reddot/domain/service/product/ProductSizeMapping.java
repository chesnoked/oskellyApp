package su.reddot.domain.service.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import su.reddot.domain.model.size.Size;

import java.math.BigDecimal;

/**
 * Использовать внимательно. В одних случаях в поле price
 * подставляется цена с комиссией, в других - без
 * @author Vitaliy Khludeev on 22.06.17.
 */
@Getter
public class ProductSizeMapping {

	private final Size size;
	private final Long count;
	private final BigDecimal price;
	private final BigDecimal priceWithCommission;

	public ProductSizeMapping(@JsonProperty("size") Size size, @JsonProperty("count") Long count, @JsonProperty("price") BigDecimal price) {
		this.size = size;
		this.count = count;
		this.price = price;
		this.priceWithCommission = null;
	}

	public ProductSizeMapping(Size size, Long count, BigDecimal price, BigDecimal priceWithCommission) {
		this.size = size;
		this.count = count;
		this.price = price;
		this.priceWithCommission = priceWithCommission != null ?
				priceWithCommission.setScale(0, BigDecimal.ROUND_UP) :
				null;

	}

	@Override
	/*
	  TODO: нужен тест
	 */
	public boolean equals(Object o) {
		if (this == o) {return true;}
		if (o == null || getClass() != o.getClass()) {return false;}

		ProductSizeMapping that = (ProductSizeMapping) o;

		if((size == null && that.size != null) || (size != null && that.size == null)) {
			return false;
		}
		if (size != null && !size.getId().equals(that.size.getId())) {return false;}

		if (!count.equals(that.count)) {return false;}

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
		result = 31 * result + count.hashCode();
		result = 31 * result + (price != null ? price.stripTrailingZeros().hashCode() : 0);
		return result;
	}
}
