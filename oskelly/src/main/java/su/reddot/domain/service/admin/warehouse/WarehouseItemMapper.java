package su.reddot.domain.service.admin.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.logistic.WaybillRepository;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.model.logistic.Waybill;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.presentation.view.admin.WarehouseItemView;
import su.reddot.presentation.view.product.ProductView;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public final class WarehouseItemMapper {

    private final ProductService productService;
    private final WaybillRepository waybillRepository;
    private final ProfileService profileService;
    private final OrderPositionRepository orderPositionRepository;

    //todo возможно стоит перенести это в WarehouseService
    WarehouseItemView mapProductItemToWarehouseItem(ProductItem productItem){

        WarehouseItemView warehouseItem = new WarehouseItemView();

        warehouseItem.setOriginalProductItem(productItem);
        //Проверки на NULL нет, т.к. у ProductItem'a обязан существовать Product, но это не точно˚
        Product itemProduct = productItem.getProduct();
        Optional<ProductView> originalProductView = productService.getProductView(itemProduct.getId(), null);
        if (!originalProductView.isPresent()) {
            log.error("Не удалось построить представление ProductView для ProductItem {}", productItem.getId());
            return null;
        }

        warehouseItem.setOriginalProduct(originalProductView.get());
        //продавец есть всегда
        warehouseItem.setSeller(profileService.getProfileView(itemProduct.getSeller().getId()));

        warehouseItem.setItemState(productItem.getState().name());
        //Вообще говоря, на склд могут попасть товары, еще не проданные (Консъерж, У нас)
        if (productItem.getEffectiveOrder() != null) {
            warehouseItem.setOriginalOrder(java.util.Optional.ofNullable(productItem.getEffectiveOrder()));
            warehouseItem.setOriginalOrderPosition(orderPositionRepository.findByOrderAndProductItem(productItem.getEffectiveOrder(),productItem));
            warehouseItem.setBuyer(java.util.Optional.ofNullable(
                    profileService.getProfileView(
                            productItem.getEffectiveOrder()
                                    .getBuyer()
                                    .getId())));
            //Сейчас мы исходим из того, что накладные могут быть только у "проданных" вещей
            warehouseItem.setWaybillList(waybillRepository.findAllByOrderPositionOrderByIdDesc(warehouseItem.getOriginalOrderPosition().get()));

            List<Waybill> waybillList = warehouseItem.getWaybillList();
            warehouseItem.setLastWaybill(!waybillList.isEmpty() ? Optional.ofNullable(waybillList.get(0)) : Optional.empty());
            warehouseItem.setLastWaybillExternalId(!waybillList.isEmpty() ? waybillList.get(0).getExternalSystemId() : "N/A");
        }
        else {
            warehouseItem.setOriginalOrderPosition(Optional.empty());
            warehouseItem.setOriginalOrderPosition(Optional.empty());
            warehouseItem.setBuyer(Optional.empty());
            warehouseItem.setLastWaybill(Optional.empty());
            warehouseItem.setLastWaybillExternalId("N/A");
        }

        return warehouseItem;
    }

}
