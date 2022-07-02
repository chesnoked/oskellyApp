package su.reddot.domain.model.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor @Getter
public class OrderCompletionReadiness {

    /** Все позиции заказа согласованы.
     * Это значит, что продажа каждой вещи подтверждена (или отклонена) продавцом,
     * каждую вещь проверили на соответствие заявленному состоянию
     * и по результату вещь либо можно отправлять продавцу, либо нет.
     * Таким образом, завершение заказа зависит не от результатов подверждения продажи вещи и проверки
     * вещи, а от факта проведения этих действий.
     **/
    private final boolean isCompleted;

    /** Ни одну позицию заказа нельзя продать по той или иной причине.
     * Проверять этот флаг имеет смысл только в случае, если {@link #isCompleted} установлен. */
    private final boolean isEntirelyUnavailable;

    /** Итоговая сумма, которую нужно списать у покупателя,
     * после того, как все позиции заказа согласованы. */
    private final BigDecimal actualAmountToPay;
}
