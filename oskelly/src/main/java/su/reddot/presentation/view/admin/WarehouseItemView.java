package su.reddot.presentation.view.admin;

import lombok.Data;
import lombok.experimental.Accessors;
import su.reddot.domain.model.logistic.Waybill;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.presentation.view.product.ProductView;

import java.util.List;
import java.util.Optional;

/**
 * Катрочка товара "для склада"
 */

@Data
@Accessors(chain = true)
public class WarehouseItemView {

    private ProductItem originalProductItem;
    //TODO нужно сразу привест к размеру вида FR-40
    private ProductView originalProduct;

    private Optional<Order> originalOrder;

    private Optional<OrderPosition> originalOrderPosition;

    private ProfileView seller;
    //Его может не быть
    private Optional<ProfileView> buyer = Optional.empty();
    /**
     * Список всех накладных для данной вещи
     */
    private List<Waybill> waybillList;

    private Optional<Waybill> lastWaybill;

    private String lastWaybillExternalId;

    private String itemState;

}
