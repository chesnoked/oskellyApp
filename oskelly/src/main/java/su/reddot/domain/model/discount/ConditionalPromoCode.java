package su.reddot.domain.model.discount;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Промо-код, сумма которого задается в абсолютном значении в рублях.
 */
@Entity
@Getter @Setter
public class ConditionalPromoCode extends AbsolutePromoCode {

    /** Минимальная цена, начиная с которой (включительно) действует промо-код. */
    private BigDecimal beginPrice;

    @Override
    public boolean supports(BigDecimal price) {
        return price.compareTo(beginPrice) > -1;
    }
}
