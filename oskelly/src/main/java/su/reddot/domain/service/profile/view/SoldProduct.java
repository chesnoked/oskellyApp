package su.reddot.domain.service.profile.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/** Данные для отображения товара в моих продажах. */
@Getter @Setter @Accessors(chain = true)
public class SoldProduct {

    private long id;

    private long itemId;

    private long orderId;

    private String description;

    private String brand;

    /** Цена, которую оплатил покупатель без учета доставки. */
    private String priceWithCommission;

    /** Цена, которую получает продавец. */
    private String priceWithoutCommission;

    /** Данные о размере. */
    private String size;

    private String primaryImage;

    private List<State> states;

    @Getter @Setter @Accessors(chain = true)
    public static class State {

        private String name;

        private String formattedAt;
    }
}
