package su.reddot.domain.model.discount;

import lombok.Getter;
import lombok.Setter;
import su.reddot.presentation.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Промо-код, сумма которого задается в абсолютном значении в рублях.
 */
@Entity
@Getter @Setter
public class AbsolutePromoCode extends PromoCode {

    /** Размер скидки в рублях, value > 0 */
    @Column(name = "absolute_value")
    private BigDecimal value;

    /**
     * Получить цену с учетом скидки. Скидка вычисляется как часть от цены.
     */
    @Override
    public BigDecimal getPriceWithDiscount(BigDecimal originalPrice) {

        BigDecimal priceWithDiscount = originalPrice.subtract(value);

        /* если скидка имеет значение, большее 100%, то возвращать в качестве стоимости заказа 0,
         * а не отрицательное значение. */
        return priceWithDiscount.signum() == 1? priceWithDiscount : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getSavingsValue(BigDecimal originalPrice) {

        BigDecimal discount = originalPrice.multiply(value);

        /* скидка не может быть больше самой стоимости */
        return originalPrice.compareTo(discount) > -1? discount : originalPrice;
    }

    @Override
    public String getFormattedValue() {
        return Utils.formatPrice(value) + " руб.";
    }
}
