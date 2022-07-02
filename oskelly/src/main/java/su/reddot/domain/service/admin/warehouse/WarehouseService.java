package su.reddot.domain.service.admin.warehouse;

import su.reddot.domain.model.product.ProductItem;
import su.reddot.presentation.view.admin.WarehouseItemView;

import java.util.List;

public interface WarehouseService {

    List<WarehouseItemView> getItemsByState(ProductItem.State state);

    List<WarehouseItemView> getItemsInStates(List<ProductItem.State> states);

    List<WarehouseItemView> getSoldItems();



    /**
     * Установить отметку о приёме на склад
     * @param itemId иднтификаор ProductItem
     */
    void takeToWarehouse(Long itemId) throws IllegalArgumentException;

    /**
     * Установить отметку о верификации
     * @param itemId
     * @throws IllegalArgumentException
     */
    void startVerification(Long itemId) throws IllegalArgumentException;

    /**
     * Установит статус по завершении верификации.
     * @param itemId
     * @param state Допустимые значения READY_TO_SHIP, химчитстка, возврат, пересогласование цены
     *              иначе генерируем ошибку
     * @throws IllegalArgumentException
     */
    void stopVerification(Long itemId, ProductItem.State state) throws IllegalArgumentException;

    void markAsReadyToShip(Long itemId) throws IllegalArgumentException;

    void createWaybillToClient(Long itemId) throws IllegalArgumentException;

    void sendToClient(Long itemId) throws IllegalArgumentException;

    /**
     * Список всех товаров готовх к отправке
     * Это состояния READY_TO_SHIP, CREATE_WAYBILL_TO_BUYER
     * @return
     */
    List<WarehouseItemView> getReadyToShipItems();

}
