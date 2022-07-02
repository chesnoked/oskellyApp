package su.reddot.domain.service.cart.impl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Accessors(chain = true)
public class Item {

    private long id;

    private long productId;

    /**
     * Ссылка на миниатюру изображения товара.
     * @apiNote null в случае ее отсутствия.
     */
    private String imageUrl;

    private String brandName;

    private String productName;
    private String productCondition;

    private Price productPrice;

    /** Товар можно купить в данный момент. */
    private boolean isAvailable;

    /** @apiNote null. если у товара нет атрибута размера. */
    private Size size;

    /** Другие размеры этого товара, которые есть в наличии. */
    private List<Size> availableSizes;

    /** @apiNote null если рекомендованная цена не указана для товара. */
    private Rrp rrp;

    @Getter @Setter @Accessors(chain = true)
    public static class Price {

        private BigDecimal raw;

        private String formatted;
    }

    @Getter @Setter @Accessors(chain = true)
    public static class Size {

        private long id;

        /** Подходящее для отображения пользователю значение размера. */
        private String value;
    }

    @Getter @Setter @Accessors(chain = true)
    public static class Rrp {

        /** Рекомендованная цена в рублях. */
        private String value;

        /** Экономия от этой цены в процентах. */
        private String savings;
    }
}
