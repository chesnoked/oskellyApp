package su.reddot.domain.service.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Используется также в {@link su.reddot.presentation.mobile.api.v1.ShoppingCartRestControllerV1}.</p>
 * <p>Изменения в представлении (в сторону удаления свойств) могут поломать мобильное приложение</p>
 */
@Getter @Setter @Accessors(chain = true)
public class CartView {

    private List<CartItem> items;

    private String totalPrice;
    private String effectivePrice;
    private String deliveryCost;
    private String effectivePriceWithDeliveryCost;

    /**
     * Для вывода доступных для заказа вещей на странице корзины
     */
    public List<CartItem> getEffectiveItems() {
        return items.stream()
                .filter(cartItem -> cartItem.isEffective)
                .collect(Collectors.toList());
    }

    /**
     * Для вывода недоступных для заказа вещей на странице корзины
     */
    public List<CartItem> getNoneffectiveItems() {
        return items.stream()
                .filter(cartItem -> !cartItem.isEffective)
                .collect(Collectors.toList());
    }
}

