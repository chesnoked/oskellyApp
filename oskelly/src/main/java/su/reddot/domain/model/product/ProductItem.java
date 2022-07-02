package su.reddot.domain.model.product;

import lombok.Data;
import su.reddot.domain.dao.LocalDateTimeConverter;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.ReserveType;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 17.06.17.
 */
@Data
@Entity
public class ProductItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Size size;

	private BigDecimal startPrice;

	/** Цена с учетом комиссии магазина */
	private BigDecimal currentPrice;
	private BigDecimal currentPriceWithoutCommission;

	// FIXME нигде не используется (на 13.09.17)
	private BigDecimal buyPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	private User buyer;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime reserveExpireTime;

	@ManyToOne(fetch = FetchType.LAZY)
	private User reserver;

	@Enumerated(EnumType.STRING)
	private ReserveType reserveType;

	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime deleteTime;

	@Enumerated(EnumType.STRING)
	private State state;

	//Серийный номер товара
	private String serialNumber;

	/**
	 * Заказ, в котором вещь была куплена
	 * (одна и та же вещь может находится одновременно в нескольких заказах,
	 * но только в одном заказе она будет куплена - в заказе того покупателя,
	 * кто первый оплатит свой заказ)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Order effectiveOrder;

	public boolean isSold() {
		return buyer != null;
	}

	public boolean isDeleted() {
		return deleteTime != null;
	}

	/**
	 * Товар забронирован кем-либо.
	 */
	public boolean isReserved() {
		return reserveExpireTime != null
				&& reserveExpireTime.isAfter(LocalDateTime.now())
				&& reserveType == ReserveType.EXPLICIT;
	}

	/**
	 * Товар находится в заказе, который пока еще не оплатили.
	 */
	public boolean isInPendingOrder() {
		return reserveType == ReserveType.IMPLICIT;
	}

	public long getReservationExpiryInMinutes() {
		return isReserved() ? ChronoUnit.MINUTES.between(LocalDateTime.now(), reserveExpireTime) : 0L;
	}

	public void sellTo(User u) {
//		TODO: разобраться с состоянием productItem, возможно нужно, возможно нет
//		this.productState = ProductState.SOLD;
		this.buyer = u;
		removeReservation();
	}

	public void removeReservation() {
		this.reserver = null;
		this.reserveType = null;
		this.reserveExpireTime = null;
	}

	/**
	 * Получить размер товара в конкретной системе размеров
	 * (российский, европейский, международный) в виде:
	 * {система размера}: {значение размера в системе}
	 * <p>
	 * Тип системы хранится у родителя товара, общего товара.
	 * <p>
	 * Товар содержит ссылку на значения размера в разных системах.
	 * Общий товар ( его родитель ) содержит конкретную систему размеров.
	 */
	public Optional<String> getConcreteSizePretty() {
		//noinspection ConstantConditions
		if (size == null) {
			return Optional.empty();
		}

		SizeType sizeType = getProduct().getSizeType();
		if (sizeType == null || sizeType == SizeType.NO_SIZE) {
			return Optional.empty(); //?
		}

		String thisProductItemSizeInConcreteSizeType = size.getBySizeType(sizeType);
		if (thisProductItemSizeInConcreteSizeType == null) {
			return Optional.empty(); //?
		}

		return Optional.of(String.format("%s: %s", sizeType.getAbbreviation(), thisProductItemSizeInConcreteSizeType));
	}

	/**
	 * @return вещь можно добавить в корзину, добавить в заказ
	 */
	public boolean isAvailable() {
		//noinspection ConstantConditions
		return product.getProductState() == ProductState.PUBLISHED
				&& state == State.INITIAL
				&& deleteTime == null;
	}

	public BigDecimal getNullableDeliveryCost() {

		boolean deliveryIsFree = currentPrice.compareTo(new BigDecimal(0)) == 1;
		boolean deliveryIsNotDefined = product.getCategory().isLifeStyle();

		BigDecimal actualDeliveryCost;
		if (deliveryIsNotDefined) {
			actualDeliveryCost = null;
		}
		else if (deliveryIsFree) {
			actualDeliveryCost = BigDecimal.ZERO;
		}
		else {
			actualDeliveryCost = new BigDecimal(800);
		}

		return actualDeliveryCost;
	}

	public enum State {

		/*
		 * Подробнее со состояниях смотри в https://reddotsu.atlassian.net/wiki/spaces/OSK/pages/8246455
		 */

		/**
		 * Начальное состояние вещи после создания товара
		 */
		INITIAL,

		/**
		 * Покупатель оплатил вещь в рамках заказа
		 */
		PURCHASE_REQUEST,

		/**
		 * Продавец подтвердил продажу вещи после того, как покупатель купил ее
		 */
		SALE_CONFIRMED,

		/**
		 * Продавец отменил продажу вещи после того, как покупатель купил ее
		 */
		SALE_REJECTED,

        /**
         * Приняли на склад (первичная приемка)
         */
        HQ_WAREHOUSE,

		/**
		 * Направили на верификацию
		 */
		ON_VERIFICATION,

		/**
		 * Вещь прошла верификацию.
		 * ## Можно списывать деньги
		 */
		VERIFICATION_OK,

		/**
		 * Вещь прошла верификацию, но требуется направить на химчистку
		 * ## Можно списывать деньги
		 */
		VERIFICATION_NEED_CLEANING,

		/**
		 * Состояние вещи не соответствует заявленному.
		 * Требуется подтверждение клиента
		 * ## Деньги не списываем!!!
		 */
		VERIFICATION_BAD_STATE_NEED_CONFIRMATION,

		/**
		 * Состояние вещи не соответсвует заявленному.
		 * Клиент подтвердил покупку, НО со скидкой.
		 * В этом кейсе, мы должны будем внести скидку в цену и списать меньше.
		 * ## Можно списывать деньги, с учетом скидки!
		 */
		VERIFICATION_BAD_STATE_BUYER_CONFIRMED,

		/**
		 * Отклонили при верификации
		 * Сюда можно попасть или сразу после ON_VERIFICATION, или после VERIFICATION_BAD_STATE_NEED_CONFIRMATION
		 * ## Деньги за вещь возвращаем покупателю
		 */
		REJECTED_AFTER_VERIFICATION,

		/**
		 * Готово к отправке покупателю. Создана накладная на перевозку, ждем Курьера
		 */
		READY_TO_SHIP,

		/**
		 * Курьер вызван, но еще не приехал
		 */
		CREATE_WAYBILL_TO_BUYER,

		/**
		 * Отправили клиенту (передали курьеру)
		 */
		SHIPPED_TO_CLIENT
	}
}
