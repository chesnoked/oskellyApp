package su.reddot.domain.service.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.order.Order;

/** Скидка уже использована в другом заказе */
@RequiredArgsConstructor @Getter
public class DiscountIsAlreadyUsedException extends Exception {

    /** Заказ, в котором скидка уже использовалась */
    private final Order order;
}
