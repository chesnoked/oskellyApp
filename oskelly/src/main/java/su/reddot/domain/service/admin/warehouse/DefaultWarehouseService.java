package su.reddot.domain.service.admin.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import su.reddot.domain.model.logistic.DestinationType;
import su.reddot.domain.model.logistic.event.SaleConfirmedEvent;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.presentation.view.admin.WarehouseItemView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
//todo cтоит отрефакторить данный сервис и сокрыть реализацию
public class DefaultWarehouseService implements WarehouseService {

    private final ProductItemService productItemService;
    private final WarehouseItemMapper warehouseItemMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<WarehouseItemView> getItemsByState(ProductItem.State state) {
        List<ProductItem> itemsByState = productItemService.getItemsByState(state);

        return itemsByState.stream()
                .map(warehouseItemMapper::mapProductItemToWarehouseItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseItemView> getItemsInStates(List<ProductItem.State> states){
        List<ProductItem> itemsByStates = productItemService.getItemsBySomeStates(states);

        return itemsByStates.stream()
                .map(warehouseItemMapper::mapProductItemToWarehouseItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarehouseItemView> getSoldItems() {
        List<ProductItem.State> states = Arrays.asList(
                ProductItem.State.HQ_WAREHOUSE,
                ProductItem.State.ON_VERIFICATION,
                ProductItem.State.VERIFICATION_OK,
                ProductItem.State.VERIFICATION_NEED_CLEANING,
                ProductItem.State.VERIFICATION_BAD_STATE_NEED_CONFIRMATION,
                ProductItem.State.VERIFICATION_BAD_STATE_BUYER_CONFIRMED,
                ProductItem.State.REJECTED_AFTER_VERIFICATION,
                ProductItem.State.CREATE_WAYBILL_TO_BUYER,
                ProductItem.State.READY_TO_SHIP);

        return this.getItemsInStates(states);
    }

    @Override
    public void takeToWarehouse(Long itemId) throws IllegalArgumentException{

        Optional<ProductItem> item = productItemService.findById(itemId);
        if(item.isPresent()){
            productItemService.setStateFirstOnWarehouse(item.get());
        }
        else {
            throw new IllegalStateException("Product Item с ID " + itemId + "не существует");
        }

    }

    @Override
    public void startVerification(Long itemId) throws IllegalArgumentException{

        Optional<ProductItem> item = productItemService.findById(itemId);
        if(item.isPresent()){
            productItemService.setStateOnVerification(item.get());
        }
        else {
            throw new IllegalStateException("Product Item с ID " + itemId + "не существует");
        }
    }

    @Override
    public void stopVerification(Long itemId, ProductItem.State state) throws IllegalArgumentException {
        //проеряем на допустимые значения
        if (state == ProductItem.State.VERIFICATION_OK ||
            state == ProductItem.State.VERIFICATION_NEED_CLEANING ||
            state == ProductItem.State.VERIFICATION_BAD_STATE_NEED_CONFIRMATION ||
            state == ProductItem.State.VERIFICATION_BAD_STATE_BUYER_CONFIRMED ||
            state == ProductItem.State.REJECTED_AFTER_VERIFICATION){

            Optional<ProductItem> item = productItemService.findById(itemId);
            if(item.isPresent()){
                productItemService.setStateAfterVerification(item.get(), state);
            }
            else {
                throw new IllegalStateException("Product Item с ID " + itemId + "не существует");
            }
        }
        else {
            throw new IllegalStateException("Product Item с ID " + itemId + "переводится в недопустимое состояние после верификации");
        }


    }

    @Override
    public void markAsReadyToShip(Long itemId) throws IllegalArgumentException {
        Optional<ProductItem> item = productItemService.findById(itemId);
        if(item.isPresent()){
            //TODO тут еще надо проверить, можем ли мы из текущего состояия переводит в READY_TO_SHIP
            productItemService.setStateAfterVerification(item.get(), ProductItem.State.READY_TO_SHIP);
        }
        else {
            throw new IllegalStateException("Product Item с ID " + itemId + "не существует");
        }
    }

    @Override
    public List<WarehouseItemView> getReadyToShipItems() {

        List<ProductItem.State> states = Arrays.asList(ProductItem.State.READY_TO_SHIP, ProductItem.State.CREATE_WAYBILL_TO_BUYER);
        List<ProductItem> itemsByStates = productItemService.getItemsBySomeStates(states);

        return itemsByStates.stream()
                .map(warehouseItemMapper::mapProductItemToWarehouseItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void createWaybillToClient(Long itemId) throws IllegalArgumentException {

        ProductItem productItem = productItemService.findById(itemId).isPresent() ? productItemService.findById(itemId).get() : null;

        if (productItem == null){ throw new IllegalArgumentException("Product Item с ID " + itemId + "не существует"); }

        Optional<OrderPosition> nullableOrderPosition = productItemService.getEffectiveOrderPosition(itemId);

        if (!nullableOrderPosition.isPresent()){ throw new IllegalArgumentException("Не удается найти Effective Order Position для ProductItem:" + itemId );}

        publisher.publishEvent(new SaleConfirmedEvent( nullableOrderPosition.get().getId(),
                DestinationType.OFFICE,
                DestinationType.BUYER));

        productItemService.setStateAfterVerification(productItem, ProductItem.State.CREATE_WAYBILL_TO_BUYER);
    }

    @Override
    public void sendToClient(Long itemId) throws IllegalArgumentException {

        ProductItem productItem = productItemService.findById(itemId).isPresent() ? productItemService.findById(itemId).get() : null;

        if (productItem == null){ throw new IllegalArgumentException("Product Item с ID " + itemId + "не существует"); }

        productItemService.setStateAfterVerification(productItem, ProductItem.State.SHIPPED_TO_CLIENT);
    }
}
