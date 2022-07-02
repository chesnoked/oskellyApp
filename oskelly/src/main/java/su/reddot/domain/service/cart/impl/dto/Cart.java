package su.reddot.domain.service.cart.impl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.DeliveryRequisite;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter @Accessors(chain = true)
public class Cart {

    private List<Item> items;

    /** Доступных вещей в корзине. */
    private int size;

    /**
     * Адрес доставки
     * @apiNote может быть null
     **/
    private DeliveryRequisite address;

    /**
     * Назначенная скидка
     * @apiNote может быть null если для корзины не назначено скидки
     **/
    private Discount discount;

    /**
     * Итоговая цена с учетом стоимости доставки и возможных скидок.
     * @apiNote может быть null если ни один товар корзины нельзя купить в данный момент
     * или если корзина пуста.
     **/
    private String finalPrice;

    /** Необязательный комментарий покупателя к заказу. */
    private String comment;

    /** Ни один товар в корзине не доступен, перейти к оплате нельзя. */
    private boolean isFaulty;

    @Getter @Setter @Accessors(chain = true)
    public static class Discount {

        private String description;

        private String code;

        /** Скидка все еще действительна */
        private boolean isValid;

        /**
         * Причина, по которой примененная скидка недействительна.
         * @apiNote может быть null
         **/
        private String invalidReason;

        /** Сумма скидки в рублях */
        private String discountValue;
    }
}
