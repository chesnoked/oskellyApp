package su.reddot.domain.service.cart;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class CartItem {

    /**
     * Идентификатор позиции товара в корзине.
     * Нужен для возможности удаления товара из корзины.
     */
    public final long itemId;

    public final long productId;

    /**
     * Ссылка на миниатюру изображения товара
     * или null в случае ее отсутствия
     */
    public final String imageUrl;

    public final String brandName;

    public final String productName;

    public final BigDecimal productPrice;
    public final BigDecimal deliveryCost;

    public final boolean isEffective;

    public final String nonEffectivenessReason;

    public final String productSize;
}
