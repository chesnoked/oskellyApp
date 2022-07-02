package su.reddot.domain.service.profile.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.List;

/** Данные для отображения оффера в личном кабинете. */
@Getter @Setter @Accessors(chain = true)
public class OffersByProduct {

    private Product product;

    private List<Offer> offers;

    private Negotiability negotiability;

    @Getter @Setter @Accessors(chain = true)
    public static class Product {

        private long id;

        private String brand;

        private String name;

        /** Ссылка на миниатюру изображения товара. */
        private String imageUrl;

        /** Форматированная текущая цена товара.
         * @apiNote null, если товар недоступен для покупки. */
        private String currentPrice;

        /** Форматированные доступные размеры товара.
         * @apiNote null, если товар недоступен или тип размера товара - {@link su.reddot.domain.model.size.SizeType#NO_SIZE} */
        private Size size;

        private boolean isNotUsedYet;
    }

    @Getter @Setter @Accessors(chain = true)
    public static class Offer {

        private long id;

        private String offeredPrice;

        /** @apiNote null, если продавец еще не отреагировал на предложение. */
        private Boolean isAccepted;

        private ZonedDateTime createdAt;
    }

    /** Данные о доступных размерах товара в форматированном виде. */
    @Getter @Setter @Accessors(chain = true)
    public static class Size {

        private String type;

        private String values;
    }

    @Getter @Setter
    public static class Negotiability {

        /** Цена уже согласована и покупатель может перейти к покупке товара по согласованной цене. */
        private boolean isNegotiated;

        /** Покупатель может продолжить торг. */
        private boolean isNegotiable;

        /** ? */
        private String optionalFailMessage;
    }

}
