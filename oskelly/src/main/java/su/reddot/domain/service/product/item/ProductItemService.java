package su.reddot.domain.service.product.item;

import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.product.ItemsSummaryBySize;
import su.reddot.domain.service.product.ProductSizeMapping;
import su.reddot.domain.service.product.item.view.ProductItemToSell;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Vitaliy Khludeev on 17.06.17.
 */
public interface ProductItemService {

	/**
	 * Попробовать найти вещь по Id
	 * @param itemId
	 * @return
	 */
	Optional<ProductItem> findById(Long itemId);
	/**
	 * Получает первую попавшуюся позицию товара
	 * @param p продукт
	 * @return позиция товара
	 * @throws NullPointerException если позиции товара отсутствуют
	 */
	ProductItem findFirstByProduct(Product p) throws NullPointerException;

	Optional<ProductItem> getForReservationWithLocking(Long productItemId);

	/**
	 * Подтвердить или отменить продажу вещи
	 * @param itemId продаваемая вещь
	 * @param seller продавец, который решает, продавать вещь или нет
	 * @param doConfirmSale true для подтверждения продажи, иначе false
	 * @param nullableSellerRequisite при подтверждении продажи - адрес получения вещи курьером,
	 *                                иначе значение параметра игнорируется
	 */
	void confirmSale(Long itemId, User seller, boolean doConfirmSale, SellerRequisite nullableSellerRequisite);

	void save(ProductItem p) throws CommissionException;

	/**
	 * Deprecated, т.к. может вернуть слишком большое количество записей
	 * Использовать getProductSizeMappings - он возвращает сгруппированные данные
	 * @param p
	 * @return
	 * @see ProductItemService#getProductSizeMappings(Product)
	 * @see ProductItemService#getAvailableItemsSummary(Product)
	 * @see ProductItemService#getAvailableItemsSummary(Product, List)
	 */
	@Deprecated
	List<ProductItem> findByProduct(Product p);

	void save(Iterable<ProductItem> productItems) throws CommissionException;

	/**
	 * @param rawProduct товар
	 * @param interestingSizes интересующие размеры
	 * @return по конкретному товару с наименьшей ценой на размер
	 */
	List<ProductItem> getItemsWithLowestPrice(Product rawProduct, List<Long> interestingSizes);

	/**
	 * Важно! В этом методе в качестве цены используется цена без комиссии
	 * @param p товар
	 * @return Набор сгруппированных записей
	 */
	Set<ProductSizeMapping> getProductSizeMappings(Product p);

	/**
	 * @param p товар
	 * @return Число доступных для покупки вещей, сгруппированных по размеру. Цена выбирается минимальной.
	 *
	 * 100 вещей размера X, минимальная цена которых равна 911
	 * 200 вещей размера Y, минимальная цена которых равна 4200
	 */
	List<ItemsSummaryBySize> getAvailableItemsSummary(Product p);

	/**
	 * {@link #getAvailableItemsSummary(Product)} по интересующим размерам
	 * @param interestingSizes список интересующих размеров
	 * @return число доступных для покупки вещей, имеющих интересующие размеры,
	 * сгруппированных по этим размерам
	 */
	List<ItemsSummaryBySize> getAvailableItemsSummary(Product p, List<Long> interestingSizes);

	/**
	 * @param productId товар, среди вещей которого нужно найти подходящую по размеру и цене
	 * @param sizeId интересующий размер
	 * @param price интересующая цена
	 * @return первую доступную для покупки вещь с данным размером и данной ценой
	 * @see ProductItemService#findFirstAvailable(Long, Long)
	 */
	@Deprecated
	Optional<ProductItem> findFirstAvailable(Long productId, Long sizeId, BigDecimal price);

	/**
	 * @param productId товар, среди вещей которого нужно найти подходящую по размеру и цене
	 * @param sizeId интересующий размер
	 * @return первую доступную для покупки вещь с данным размером и данной ценой
	 */
	Optional<ProductItem> findFirstAvailable(Long productId, Long sizeId);

	/**
	 *
	 * @param p товар
	 * @param s размер
	 * @param price Важно! Это цена, указанная продавцом. Без комиссии
	 * @return количество
	 */
	Long countByProductAndSizeAndPrice(Product p, Size s, BigDecimal price);

	/**
	 * Удаляет (ставит deleteTime) все найденные записи
	 * @param p товар
	 * @param s размер
	 * @param price Важно! Это цена, указанная продавцом. Без комиссии
	 */
	void deleteByProductAndSizeAndPrice(Product p, Size s, BigDecimal price, Integer limit);

	/**
	 * @param item вещь
	 * @return вещь, за исключением переданной, которую можно добавить в корзину и заказ
	 */
	Optional<ProductItem> getItemLikeThisThatCanBeOrdered(ProductItem item);

	/**
	 * @param item вещь
	 * @return вещь, которую можно купить за данную сумму
	 */
	Optional<ProductItem> getItemForSale(ProductItem item, BigDecimal interestingPrice);

	/**
	 * @return данные о вещи, если она находится в состоянии ожидания подтверждения заказа
	 */
	Optional<ProductItemToSell> getForSaleConfirmation(Long id, User user);

	/**
	 * @param state Состояние, по которому нужно искать
	 * @return список вещей, восзможно пустой.
	 */
	List<ProductItem> getItemsByState(ProductItem.State state);

	List<ProductItem> getItemsByStateAndProduct(ProductItem.State state, Product product);

	/**
	 * Получаем ProductItem'ы по нескольким состяниям
	 * @param states
	 * @return
	 */
	List<ProductItem> getItemsBySomeStates(List<ProductItem.State> states);

	/**
	 * Вызвать при первичной приёмке на склад
	 * @param item
	 */
	void  setStateFirstOnWarehouse(ProductItem item);

	/**
	 * вызвать при начале верификации
	 * @param item
	 */
	void setStateOnVerification(ProductItem item);

	/**
	 * Обновить стстус после верификации. Логика проверкти допустимости статуса в WarehouseService
	 * @param item
	 * @param state
	 */
	void setStateAfterVerification(ProductItem item, ProductItem.State state);

	/**
	 * Находит, какой orderPosition эффективного заказа ссылается на данный ProductItem
	 * @param itemId - ProductItem
	 * @return
	 */
	Optional<OrderPosition> getEffectiveOrderPosition(Long itemId);

	/**
	 * находит максимальный startPrice, который был у товара
	 * @param product
	 * @return
	 */
	BigDecimal getMaxStartPriceByProduct(Product product);
}
