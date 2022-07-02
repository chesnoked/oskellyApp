package su.reddot.domain.model.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Inheritance
@Getter @Setter @Accessors(chain = true)
public abstract class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Уникальный код */
    private String code;

    /** Срок действия промо-кода */
    @Convert(converter = ZonedDateTimeConverter.class)
    @Getter(AccessLevel.NONE)
    private ZonedDateTime expiresAt;

    public boolean isExpired() {
        //noinspection ConstantConditions
        return expiresAt != null
                && ZonedDateTime.now().compareTo(expiresAt) > 0;
    }

    /** Цена заказа с учетом скидки. */
    public abstract BigDecimal getPriceWithDiscount(BigDecimal originalPrice);

    /** Размер суммы, которую экономит владелец заказа, оплачивая его с помощью скидки.
     * То есть разница между начальной ценой и ценой со скидкой.
     */
    public BigDecimal getSavingsValue(BigDecimal originalPrice) {
        return originalPrice.subtract(getPriceWithDiscount(originalPrice));
    }

    /** Значение скидки в читаемом виде. */
    public abstract String getFormattedValue();

    /** Применима ли скидка для данной цены. */
    public boolean supports(BigDecimal price) { return true; }
}
