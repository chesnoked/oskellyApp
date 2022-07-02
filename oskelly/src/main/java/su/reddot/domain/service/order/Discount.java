package su.reddot.domain.service.order;

import lombok.RequiredArgsConstructor;

/**
 * Данные, которые отдаются клиенту в качестве результата применения скидки.
 */
@RequiredArgsConstructor
public class Discount {

    public final String optionalText;

    public final String code;

    /** Скидка все еще действительна */
    public final boolean isValidYet;

    /** Сумма скидки */
    public final String discountValue;

    /** Сумма в рублях, округленная до копеек,
     * которую экономит покупатель, используя скидку для оплаты заказа
     **/
    public final String savingsValue;

    /** Новая стоимость заказа с учетом скидки, но без учета доставки */
    @SuppressWarnings("unused") // see order/page.html
    public final String updatedOrderAmount;

    /** Конечная стоимость заказа в рублях с учетом скидки и доставки */
    public final String finalOrderAmount;

}
