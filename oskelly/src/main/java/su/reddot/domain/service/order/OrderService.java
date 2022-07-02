package su.reddot.domain.service.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.event.ProductItemSaleResolved;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.order.view.OrderView;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {

	/**
	 * Создает заказ из указанных товаров.
	 * Если хотя бы один товар в заказ добавить нельзя,
	 * то этот заказ не формируется и возникает {@link OrderCreationException}
	 * @param u пользователь, который формирует заказ
	 * @param productItems список вещей, из которых нужно сформировать заказ.
	 */
	Order createOrder(User u, Set<ProductItem> productItems);

	/**
	 * Получить данные заказа конкретного пользователя
	 * в подходящей для отображения форме
	 * @param orderId идентификатор заказа
	 * @param u покупатель, который оформил заказ
	 * @return представление заказа
	 */
	Optional<OrderView> getUserOrder(Long orderId, User u);

	/**
	 * Получить данные всех заказов конкретного пользователя
	 * в подходящей для отображения форме
	 * @param u покупатель, который оформил заказ
	 * @return список представлений заказов
	 */
	List<OrderView> getOrders(User u);
	List<OrderView> getOrders(User u, List<OrderState> interestingStates);

	/**
	 * Инициировать процесс оплаты заказа методом резервирования средств на счете.
     *
	 * @param order информация о заказе, который нужно оплатить
	 * @param user пользователь, который оплачивает заказ
	 * @return строка запроса, передается в банк для оплаты заказа.
	 * @throws IllegalArgumentException если заказа с заданным идентификатором не существует,
	 * или заказ не предполагает оплаты ( например когда заказ до этого уже был отменен ).
	 * Является ошибкой клиента, так как клиент не предоставил правильный идентификатор заказа.
	 */
	String initHold(OrderToPayFor order, User user);

	/**
	 * {@link #initHold(Long, OrderToPayFor, User)}
	 */
	@Deprecated
	String initHold(Long id, User user);

	/**
	 * Задать данные о доставке для заказа
	 * @param orderId идентификатор заказа
	 * @param requisite данные о доставке
	 * @param u пользователь, который оформляет заказ
	 */
	void setDeliveryRequisite(Long orderId, DeliveryRequisite requisite, User u);

	/**
     * Когда продавец подтверждает или отклоняет продажу вещи,
	 * обновить состояние заказа.
	 * Метод выполняется <i>после</i> завершения транзакции, которая послала этому методу событие.
	 * Это значит, что ошибки, которые возникнут при выполнении этого метода не повлияют
	 * на обработку решения продавца о продаже вещи.
	 * @param e событие с данными о вещи, которую продавец согласился или отказался продавать
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@SuppressWarnings("unused") /* вызывается неявно при появлении соответствующего события */
	void handleSaleResolution(ProductItemSaleResolved e);

	/**
	 * @param order заказ, в котором нужно найти указанную вещь
	 * @param item вещь, которую нужно найти в заказе
     * @return данные о позиции заказа, которая содержит указанную вещь
	 */
	Optional<OrderPosition> findOrderPositionWithGivenItem(Order order, ProductItem item);

	void saveOrderPosition(OrderPosition orderPosition);

	/**
     * Добавить скидку к заказу.
	 * Хранимая стоимость заказа при этом не меняется.
	 * Изменить ее с учетом примененной скидки можно только через {@link Order#applyDiscount()}
	 *
	 * @param id номер заказа
	 * @param code код скидки
     *
	 * @return данные о заказе с учетом скидки, если таковой существует
	 *
	 * @throws NotFoundException если заказ или скидка с таким кодом не существует
	 * @throws DiscountIsAlreadyUsedException если скидка уже использована в другом заказе этого пользователя
	 * @throws OrderException если нельзя применить скидку (например, когда для заказа уже назначена другая скидка).
	 * Возникает по вине клиента, когда тот нарушает контракт сервиса
	 * (нельзя повторно назначать скидку на заказ, который уже использует другую скидку).
	 *
	 */
	Discount addDiscount(Long id, User u, String code)
			throws NotFoundException, DiscountIsAlreadyUsedException, OrderException;
	Discount addDiscount(String code, Order order) throws DiscountIsAlreadyUsedException, NotFoundException, OrderException;

	/**
	 * Отменить назначенную ранее на заказ скидку.
	 * @param id идентификатор заказа, для которого нужно отменить скидку
	 * @param u покупатель заказа
     *
	 * @throws NotFoundException если заказ не существует
	 * @throws OrderException если нельзя удалить скидку (например, когда для оплаты заказа у покупателя уже зарезервированы деньги).
	 */
	void removeDiscount(Long id, User buyer)
			throws NotFoundException, OrderException;

	@Getter @Setter @Accessors(chain = true)
	class OrderToPayFor {

		Long id;

		List<Long> availablePositions;

		List<Long> unavailablePositions;
	}

}
