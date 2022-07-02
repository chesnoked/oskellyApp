package su.reddot.domain.service.order.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Accessors(chain = true)
@RequiredArgsConstructor
public class OrderItem {

    private final long id;

    private final long productId;

    private final long productItemId;

    /**
     * Ссылка на миниатюру изображения товара
     * или null в случае ее отсутствия
     */
    private final String imageUrl;

    private final String brandName;

    private final String productName;

    private final BigDecimal productPrice;

    private final String productSize;

    private final String sellerNick;

    private final Long sellerId;

    private final String state;

    private final BigDecimal deliveryCost;

    /**
     * <p>Для нового, еще не оплаченного заказа:
     * <ul>
     * <li>true, если товар, который ранее добавили в заказ,
     * на момент формирования данных о заказе все еще доступен для покупки;</li>
     * <li>false, если товар для покупки уже недоступен.</li>
     * </ul>
     * </p>
     * <p>Если заказ уже оплатили, свойство равно null.</p>
     **/
    private boolean isAvailable;

    /** История состояний позиции заказа. */
    private List<State> allStates;

    @Getter @Setter @Accessors(chain = true)
    public static class State {

        private String name;

        private String at;
    }
}
