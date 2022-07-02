package su.reddot.domain.service.admin.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.service.order.view.OrderItem;
import su.reddot.domain.service.profile.ProfileView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class AdminOrderView {
    private final Long id;

    private final List<OrderItem> items = new ArrayList<>();

    private final BigDecimal price;

    private final String state;

    private final String stateName;

    private final String lastUpdateTime;

    private final ProfileView buyerProfile;

    /* Пользователь может перейти к оплате заказа */
    private final boolean isPayable;

    /**
     * Заказ в данный момент оплачивается:
     * пользователь инициировал оплату, но
     * платежный шлюз еще не прислал результат этой операции.
     */
    private final boolean isPaidAtThisMoment;

    private final DeliveryRequisite deliveryRequisite;

    /**
     * @return стоимость заказа с точностью до рублей (исключительно для отображения)
     */
    public BigDecimal getPrice() {
        return price.setScale(0, RoundingMode.DOWN);
    }
}
