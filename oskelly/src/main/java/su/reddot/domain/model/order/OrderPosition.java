package su.reddot.domain.model.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.reddot.domain.dao.LocalDateTimeConverter;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 22.04.17.
 */
@Entity
@Getter @Setter
public class OrderPosition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProductItem productItem;

	private BigDecimal commission;

	/** Стоимость товара без учета доставки */
	private BigDecimal amount;

	/** Стоимость доставки.
	 * @apiNote
	 * <ul>Равняется:
	 *     <li>нулю, если доставка бесплатна для покупателя</li>
	 *     <li>null, если стоимость доставки расчитать нельзя, и магазин согласовывает ее вручную</li>
     *</ul>
	 */
	private BigDecimal deliveryCost;

	/** @apiNote пока не используется: состояние вычисляется на основе состояния вещи,
	 * на которую ссылается позиция заказа. */
	@Enumerated(EnumType.STRING)
	private OrderPositionState state;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime stateTime;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "firstName",    column = @Column(name = "pickup_first_name")),
		@AttributeOverride(name = "lastName",     column = @Column(name = "pickup_last_name")),
		@AttributeOverride(name = "companyName",  column = @Column(name = "pickup_company_name")),
		@AttributeOverride(name = "phone",        column = @Column(name = "pickup_phone")),
		@AttributeOverride(name = "zipCode",      column = @Column(name = "pickup_zip_code")),
		@AttributeOverride(name = "city",         column = @Column(name = "pickup_city")),
		@AttributeOverride(name = "address",      column = @Column(name = "pickup_address"))
	})
	private SellerRequisite pickupRequisite;

	/**
	 * Вещь попала в итоговый заказ, за который у клиента уже окончательно списали деньги.
	 */
	private Boolean isEffective;

	/**
	 * На момент начала оплаты заказа вещь была доступна,
	 * и ее стоимость включается в стоимость заказа.
	 */
	private Boolean participatesInPayment;

	/**
	 * Итоговая стоимость позиции заказа вместе с доставкой
	 **/
	public BigDecimal getEffectiveAmount() {
		//noinspection ConstantConditions стоимость доставки может быть не задана (не путать с нулевой стоимостью доставки)
		return deliveryCost != null? amount.add(deliveryCost) : amount;
	}

	/** Статус позиции товара. Не задан, если заказ создан и еще не оплачен. */
	public Optional<State> getState() {

		OrderState orderState = order.getState();

		if (orderState.isOrderIsNotPayedYet()) { return Optional.empty(); }

		boolean productItemIsAvailable = productItem.getProduct().getProductState() == ProductState.PUBLISHED
                && !productItem.isDeleted();
		boolean productItemIsUnavailableOrAlreadyInAnotherOrder
				= !productItemIsAvailable
                    || productItem.getEffectiveOrder() != null
                    && !productItem.getEffectiveOrder().getId().equals(order.getId());

		if (productItemIsUnavailableOrAlreadyInAnotherOrder) {
			return Optional.of(State.UNAVAILABLE);
		}

		return from(productItem.getState());
	}

	public Optional<OrderPosition.State> from(ProductItem.State s) {

		State currentOrderPositionState = null;

		if (s == ProductItem.State.PURCHASE_REQUEST) {
			currentOrderPositionState = State.PURCHASE_REQUEST_SENT;
		}
		else if (s == ProductItem.State.SALE_CONFIRMED) {
			currentOrderPositionState = State.SALE_CONFIRMED;
		}
		else if (s == ProductItem.State.SALE_REJECTED) {
			currentOrderPositionState = State.SALE_DECLINED;
		}
		else if (s == ProductItem.State.HQ_WAREHOUSE) {
			currentOrderPositionState = State.ARRIVED_AT_WAREHOUSE;
		}
		else if (s == ProductItem.State.ON_VERIFICATION
				|| s == ProductItem.State.VERIFICATION_NEED_CLEANING) {
			currentOrderPositionState = State.VERIFICATION_PENDING;
		}
		else if (s == ProductItem.State.VERIFICATION_OK
				|| s == ProductItem.State.READY_TO_SHIP
				|| s == ProductItem.State.CREATE_WAYBILL_TO_BUYER
				) {
			currentOrderPositionState = State.VERIFICATION_PASSED;
		}
		else if (s == ProductItem.State.REJECTED_AFTER_VERIFICATION) {
			currentOrderPositionState = State.VERIFICATION_FAILED;
		}
		else if (s == ProductItem.State.SHIPPED_TO_CLIENT) {
			currentOrderPositionState = State.SHIPPED_TO_CLIENT;
		}

		return Optional.ofNullable(currentOrderPositionState);

	}

	/** @see <a href="https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">
	 * Vlad Mihalcea's related post
	 * </a> */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderPosition)) return false;

		OrderPosition that = (OrderPosition) o;

		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() { return 31; }

	@RequiredArgsConstructor @Getter
	public enum State {

		/** Товар сняли с продажи / вещь уже купил другой пользователь */
		UNAVAILABLE("Нет в продаже", StateGroup.REJECTED),

		/** Продавец получил уведомление о покупке товара. */
		PURCHASE_REQUEST_SENT("Ожидаем подтверждение продажи товара", StateGroup.NEW),

		/**
		 * Продавец подтвердил продажу товара в ответ на запрос покупки.
		 */
		SALE_CONFIRMED("Продавец подтвердил продажу товара", StateGroup.CURRENT),

		/**
		 * Продавец отменил продажу товара в ответ на запрос покупки.
		 */
		SALE_DECLINED("Продавец отменил продажу товара", StateGroup.REJECTED),

		ARRIVED_AT_WAREHOUSE("Товар отправлен на экспертизу OSKELLY", StateGroup.CURRENT),

		VERIFICATION_PENDING("Товар проходит экспертизу OSKELLY", StateGroup.CURRENT),

		VERIFICATION_PASSED("Товар прошел экспертизу OSKELLY и готов к отправке", StateGroup.CURRENT),

		VERIFICATION_FAILED("Товар не прошел экспертизу OSKELLY, возврат денег", StateGroup.REJECTED),

		SHIPPED_TO_CLIENT("Товар отправлен", StateGroup.CURRENT),

		ARRIVED_TO_CLIENT("Товар доставлен", StateGroup.CURRENT);

		/** Понятное человеку описание состояния позиции заказа. */
		private final String description;

		private final StateGroup group;
	}

	@RequiredArgsConstructor @Getter
	public enum StateGroup {

		NEW("Новые", 1),

		CURRENT("Текушие", 2),

		REJECTED("Отказы", 3),

		COMPLETED("Завершенные", 4);

		private final String description;

		private final Integer sort;
	}

}
