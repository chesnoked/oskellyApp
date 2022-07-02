package su.reddot.domain.model.discount;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Промокод, сумма которого вычисляется как часть цены.
 */
@Entity
@Getter @Setter
public class FractionalPromoCode extends PromoCode {

    /** Размер скидки в виде десятичной дроби, 0 <= value <= 1 */
    protected BigDecimal value;

    /**
     * Получить цену с учетом скидки. Скидка вычисляется как часть цены.
     */
    @Override
    public BigDecimal getPriceWithDiscount(BigDecimal originalPrice) {

        BigDecimal priceWithDiscount = originalPrice.subtract(originalPrice.multiply(value));

        /* если скидка имеет значение, большее 100%, то возвращать в качестве стоимости заказа 0,
         * а не отрицательное значение. */
        return priceWithDiscount.signum() == 1? priceWithDiscount : BigDecimal.ZERO;
    }

    @Override
    public String getFormattedValue() {
        return value.multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros().toPlainString() + " %";
    }
}

