package su.reddot.domain.service.orderPosition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.order.OrderPosition;

import java.math.BigDecimal;

/**
 * @author Vitaliy Khludeev on 15.12.17.
 */
@RequiredArgsConstructor
@Getter
public class SaleView {

	/**
	 * Идентификатор позиции заказа
	 */
	private final Long id;
	private final String orderId;
	private final Long productId;
	private final Long productItemId;
	private final String brandName;
	private final String productName;
	private final String size;
	private final BigDecimal price;
	private final BigDecimal buyPrice;
	private final BigDecimal buyPriceWithoutCommission;
	private final String image;
	private final String stateName;
	private final boolean needSaleConfirm;
}
