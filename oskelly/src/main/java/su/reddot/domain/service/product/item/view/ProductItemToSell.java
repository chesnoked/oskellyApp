package su.reddot.domain.service.product.item.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.service.image.ProductImage;

import java.util.Collections;
import java.util.List;

/**
 *  Представление для отображения страницы
 */
@Getter @Setter @Accessors(chain = true)
public class ProductItemToSell {

    private long itemId;

    private long orderId;

    private String brand;

    private String category;

    private String condition;

    private ProductImage primaryImage;
    private List<ProductImage> additionalImages = Collections.emptyList();

    private List<Attribute> attributes = Collections.emptyList();

    private String size;

    private String priceWithCommission;

    private String priceWithoutCommission;

    private ConfirmationResult confirmationResult;

    private SellerRequisite pickupRequisite;

    private boolean requisiteIsEditable;

}
