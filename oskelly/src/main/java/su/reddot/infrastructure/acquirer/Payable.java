package su.reddot.infrastructure.acquirer;

import su.reddot.domain.model.user.User;

import java.math.BigDecimal;

/**
 * Чтобы оплатить заказ, тот должен предоставлять уникальный идентификатор
 * и сумму оплаты.
 */
public interface Payable {

    /**
     * Получить идентификатор заказа. Идентификатор нужен для того, чтобы
     * можно было повторить оплату этого заказа, если первая попытка оплаты
     * окажется неудачной.
     * @return идентификатор заказа.
     */
    String getOrderId();

    /**
     * Получить сумму оплаты в рублях.
     * @return сумма оплаты в рублях.
     */
    BigDecimal getPaymentAmount();

    /**
     * Получить покупателя
     * @return покупатель
     */
    User getBuyer();
}
