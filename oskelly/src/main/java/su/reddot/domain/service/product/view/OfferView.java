package su.reddot.domain.service.product.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.service.image.ProductImage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter @Setter @Accessors(chain = true)
public class OfferView {

    private long id;

    private String brand;

    private String category;

    private String condition;

    private ProductImage primaryImage;
    private List<ProductImage> additionalImages = Collections.emptyList();

    private Map<String, String> attributes = Collections.emptyMap();

    /* Цена товара с на сайте с учетом комиссии */
    private String currentPrice;

    /* Цена, которую предложил пользователь */
    private String offeredPrice;

    /* Цена, которую получит продавец, если примет предложение о снижении цены */
    private String offeredPriceWithoutCommission;

    private Boolean isAccepted;
}
