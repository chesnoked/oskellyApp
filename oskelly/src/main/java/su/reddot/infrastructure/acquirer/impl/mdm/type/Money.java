package su.reddot.infrastructure.acquirer.impl.mdm.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Структура денежного типа спецификации MdmPay
 */
@Getter @Setter @ToString
@NoArgsConstructor
public class Money {

    /**
     * Сумма в основных ед. валюты с возможным разделителем '.' для копеек.
     * Три и более десятичных знака приведут к ошибке.
     * Примеры: '10', '0.15', '12.1', '199.30', '0.55' и т.д.
     * Для подписи формат суммы '0.00', то есть всегда содержит два десятичных знака и не содержит
     * лидирующих нулей.
     * Примеры 'amount.value=10.00', 'amount.value=0.15', 'amount.value=12.10' и т.д.
     */
    private BigDecimal value;

    /**
     * Валюта платежа. Возможные значения: RUB
     */
    private String currency = "RUB";

    public Money(BigDecimal value) {
        this.value = value.setScale(2, RoundingMode.UP);
    }
}
