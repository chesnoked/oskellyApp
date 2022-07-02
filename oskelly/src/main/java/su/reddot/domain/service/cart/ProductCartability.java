package su.reddot.domain.service.cart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductCartability {

    /** Товар можно добавить в корзину */
    public final boolean productIsCartable;

    /**
     * Причина, по которой товар нельзя добавить в корзину
     *
     * @apiNote может быть null
     **/
    public final String nonCartabilityReason;

    public ProductCartability(boolean productIsCartable) {
        this(productIsCartable, null);
    }
}
