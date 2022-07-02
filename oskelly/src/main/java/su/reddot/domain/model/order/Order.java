package su.reddot.domain.model.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.Discount;
import su.reddot.infrastructure.acquirer.Payable;
import su.reddot.infrastructure.cashregister.Checkable;
import su.reddot.presentation.Utils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.UP;

@Entity
@Table(name = "\"order\"")
@Getter @Setter @NoArgsConstructor
public class Order implements Payable, Checkable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private UUID uuid;

	/**
	 * Идентификатор транзакции для завершение расчета.
     * Выдается банком в ответ на запрос резервирования средств.
	 */
	private String transactionId;

	/**
	 * Стоимость заказа, которая состоит из стоимости всех позиций и стоимости доставки каждой позиции.
	 * При оплате заказа именно на эту сумму резервируются средства у покупателя.
	 */
	private BigDecimal amount;

	/**
	 * Итоговая стоимость заказа, которая получается после проверки всех вещей заказа
	 * на то, что их вообще можно купить. Включает стоимость доставки.
	 * Эта сумма окончательно списывается у покупателя после предварительного резервирования средств на оплату всего заказа.
	 * Может быть меньше {@link #amount}
	 */
	private BigDecimal effectiveAmount;

	/** Товары, которые пользователь добавил в заказ на момент его создания. */
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderPosition> orderPositions = new ArrayList<>();

	/** Позиции заказа, продажу вещей в которых подтвердил продавец. */
	@OneToMany(mappedBy = "order")
	@Where(clause = "is_effective = true")
	private List<OrderPosition> effectiveOrderPositions;

	/** С момента создания заказа часть товаров в нем может перестать быть доступной для покупки.
	 * Перед началом оплаты те товары, которые доступны для покупки, помечаются отдельно.
	 * Товары заказа, за которые мы зарезервировали деньги у покупателя. */
	@OneToMany(mappedBy = "order")
	@Where(clause = "participates_in_payment = true")
	private List<OrderPosition> participatedInPaymentOrderPositions;

	@ManyToOne(fetch = FetchType.LAZY)
	private User buyer;

	/**
	 * Обновляется только через {@link #setState(OrderState)}
	 * с целью упрощения интерфейса класса.
	 */
	@Convert(converter = ZonedDateTimeConverter.class)
    @Setter(AccessLevel.NONE)
	private ZonedDateTime stateTime;

	@Convert(converter = ZonedDateTimeConverter.class)
	@Setter(AccessLevel.NONE)
	private ZonedDateTime createTime;

	@Enumerated(EnumType.STRING)
	private OrderState state;

	/**
	 * Данные по доставке.
	 *
     * По-умолчанию берутся из профиля пользователя.
	 * В профиле эти данные не являются обязательными,
	 * поэтому только что созданный заказ также может их не иметь.
	 *
     * Пользователь для заказа может выставить отдельные данные доставки,
	 * отличные от тех, что есть у него в профиле.
	 */
	@Embedded
	private DeliveryRequisite deliveryRequisite;

	/**
	 * Фискальный чек для покупателя заказа.
	 */
	private String buyerCheck;

	/** Необязательный скидочный промо-код.
	 * Изменять его значение можно только через {@link #setDiscount(PromoCode)}.
	 **/
	@ManyToOne(fetch = FetchType.LAZY)
	private PromoCode promoCode;

	/** <p>Покупатель применил для оплаты заказа подарочный сертификат.</p>
	 * <p>Для оплаты заказа покупатель может использовать либо промо-код, либо сертификат.
     * Одновременно для оплаты их использовать нельзя.</p>
	 * <p>Скрыт за интерфейсом скидки {@link #setDiscount(GiftCard)}, {@link #dropDiscount()};
	 * поэтому отдельных геттеров/сеттеров не имеет.</p>
	 **/
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL, optional = false)
	private GiftCard giftCard;

	/** Заказ может оформить анонимный пользователь. */
	private String guestToken;

	/** Пользователь может оставить комментарий к заказу. */
	private String comment;

	@Override
	public String getOrderId() { return Long.toString(id); }

	@Override
	public BigDecimal getPaymentAmount() {
		return amount;
	}

	public void setState(OrderState newState) {
		state = newState;
		stateTime = ZonedDateTime.now();

		if (newState == OrderState.CREATED) {
			createTime = ZonedDateTime.now();
		}
	}

	/**
	 * Обновить статус заказа - банк успешно зарезервировал средства покупателя
	 * для оплаты заказа.
	 * @param tranId номер транзакции операции резервирования, по которому
	 *                      можно завершить расчет окончательно.
	 */
    public void setAsPaid(String tranId) {
    	setState(OrderState.HOLD);
		transactionId = tranId;
    }

	/**
	 * Обновить статус заказа - во время резервирования средств на оплату заказа произошла ошибка,
	 */
	public void setAsFailed() {
		state = OrderState.HOLD_ERROR;
		stateTime = ZonedDateTime.now();
	}

	/** Пользовать может перейти к этапу заполнения данных для оплаты заказа */
	public boolean isPayable() { return state == OrderState.CREATED; }

	/** Все данные для оплаты заказа заполнены правильно и теперь заказ можно оплатить.
	 * Более строгое условие возможности оплаты по сравнению с {@link #isPayable()}
	 * */
	public boolean isReadyForPayment() {

	    /* Ты не можешь оплатить заказ, если на него назначена скидка,
	    * у который истек срок действия. Ты должен вручную снять скидку с заказа
	    * для продолжения оплаты. */
		boolean discountIsValid = promoCode == null || !promoCode.isExpired();

		//noinspection ConstantConditions
		boolean deliveryRequisiteIsComplete =  deliveryRequisite != null && deliveryRequisite.complete();

		return (state == OrderState.CREATED || state == OrderState.HOLD_PROCESSING)
				&& discountIsValid && deliveryRequisiteIsComplete;

	}

	/**
	 * Проверить, оплачивается ли заказ в данный момент.
	 * Под "оплачивается" понимается состояние заказа, в котором тот находится,
	 * когда пользователь уже инициировал оплату, но
	 * платежный шлюз еще не прислал результат этой операции.
	 */
	public boolean isPaidAtThisMoment() {
	 	return state == OrderState.HOLD_PROCESSING;
	}

	public void setAsInPaymentProcess() {
	    state = OrderState.HOLD_PROCESSING;
	}

	public BigDecimal getDeliveryCost() {
		List<OrderPosition> actualOrderPositions
				= state == OrderState.CREATED ? orderPositions
				: participatedInPaymentOrderPositions;

		return actualOrderPositions.stream()
				.map(OrderPosition::getDeliveryCost)
				.filter(Objects::nonNull)
				.reduce(ZERO, BigDecimal::add);
	}

	/**
	 * Применяет скидку к заказу, при этом не меняя его стоимости.
	 * @param promoCode (not null) промо-код, который нужно применить к заказу
	 * @throws OrderException если для заказа нельзя применить указанный промо-код с текстом причины
	 */
	public void setDiscount(PromoCode promoCode) throws OrderException, NotFoundException {

		//noinspection ConstantConditions
		boolean orderIsDiscountable = state == OrderState.CREATED && giftCard == null;
		if (!orderIsDiscountable) {
			throw new OrderException("К этому заказу нельзя применить скидку");
		}

		if (promoCode.isExpired()) {
			throw new NotFoundException("Скидка c кодом: " + promoCode.getCode() + " не найдена");
		}

		this.promoCode = promoCode;
	}

	/**
	 * Применяет сертификат к заказу, при этом <b>не меняя</b> его стоимости.
	 * @param card (not null) сертификат, который применяется для оплаты заказа
	 * @throws OrderException если для заказа нельзя применить указанный сертификат
	 * / отменить установленный ранее сертификат, с указанием причины.
	 */
	public void setDiscount(GiftCard card) throws OrderException, NotFoundException {

		//noinspection ConstantConditions
		boolean orderIsDiscountable = state == OrderState.CREATED
				&& promoCode == null;

		if (!orderIsDiscountable) {
			throw new OrderException("К этому заказу нельзя применить скидку");
		}

		if (card.getState() != GiftCard.State.PAYED || card.isExpired()) {
			throw new NotFoundException("Скидка c кодом: " + card.getCode() + " не найдена");
		}

		giftCard = card;
		card.setOrder(this);
	}

	public void setOrderPosition(OrderPosition op) {
		op.setOrder(this);
		orderPositions.add(op);
	}

	@Override
	public String getRequestId() { return id.toString(); }

	@Override
	public BigDecimal getNonCashAmount() {
		return effectiveAmount.setScale(2, UP);
	}

	@Override
	public List<Line> getLines() {

		ArrayList<Line> cookedLines = new ArrayList<>();

        BigDecimal original = effectiveOrderPositions.stream()
                .map(OrderPosition::getAmount)
                .reduce(ZERO, BigDecimal::add);

        BigDecimal discountInFractions = original.subtract(effectiveAmount).divide(original, 2, UP);
        BigDecimal acc                 = BigDecimal.ZERO;

        for (Iterator<OrderPosition> iter = effectiveOrderPositions.iterator(); iter.hasNext(); ) {
            OrderPosition orderPosition = iter.next();

            BigDecimal originalAmount     = orderPosition.getAmount();
            BigDecimal amountWithDiscount = originalAmount
                    .subtract(originalAmount.multiply(discountInFractions))
                    .setScale(2, UP);
            acc = acc.add(amountWithDiscount);

            if (!iter.hasNext() && acc.compareTo(effectiveAmount) < 0) {
                amountWithDiscount = amountWithDiscount.add(effectiveAmount.subtract(acc));
            }

			ProductItem productItem = orderPosition.getProductItem();

			String description = String.format("%s %d", productItem.getProduct().getDisplayName(),
					productItem.getId());

			Line checkLine = new Line(1, amountWithDiscount, description);
			cookedLines.add(checkLine);

			/* Отдельно от стоимости позиции указывать стоимость доставки */
			BigDecimal deliveryCost = orderPosition.getDeliveryCost() != null?
					orderPosition.getDeliveryCost()
					: ZERO;
			Line deliveryCostLine = new Line(1, deliveryCost, String.format("Доставка %s", description));
			cookedLines.add(deliveryCostLine);
        }

		return cookedLines;
	}

	public Map<OrderPosition, Line> getLinesWithPositions() {

		Map<OrderPosition, Line> cookedLines = new LinkedHashMap<>();

		BigDecimal original = effectiveOrderPositions.stream()
				.map(OrderPosition::getAmount)
				.reduce(ZERO, BigDecimal::add);

		BigDecimal discountInFractions = original.subtract(effectiveAmount).divide(original, 2, UP);
		BigDecimal acc                 = BigDecimal.ZERO;

		for (Iterator<OrderPosition> iter = effectiveOrderPositions.iterator(); iter.hasNext(); ) {
			OrderPosition orderPosition = iter.next();

			BigDecimal originalAmount     = orderPosition.getAmount();
			BigDecimal amountWithDiscount = originalAmount
					.subtract(originalAmount.multiply(discountInFractions))
					.setScale(2, UP);
			acc = acc.add(amountWithDiscount);

			if (!iter.hasNext() && acc.compareTo(effectiveAmount) < 0) {
				amountWithDiscount = amountWithDiscount.add(effectiveAmount.subtract(acc));
			}

			ProductItem productItem = orderPosition.getProductItem();

			String description = String.format("%s %d", productItem.getProduct().getDisplayName(),
					productItem.getId());

			Line checkLine = new Line(1, amountWithDiscount, description);

			cookedLines.put(orderPosition, checkLine);
		}

		return cookedLines;
	}

	/** <p>Проверить, готов ли заказ к завершению оплаты:
	 * можно ли завершать расчет с покупателем после того, как
	 * магазин зарезервировал у него всю сумму заказа, или же
	 * еще есть позиции заказа, продажа товаров в которых
	 * пока не согласована с продавцом.</p>
	 * <p>Под завершением расчета понимается либо завершение холда
	 * с полным или частичным списанием либо полный возврат средств
	 * в случае, когда ни одну позицию заказа выкупить не получается.</p>
	 */
	public OrderCompletionReadiness getCompletionReadiness() {

	    boolean isCompleted = true;
		boolean atLeastOneItemIsConfirmedForSale = false;

		/* стоимость всех согласованных позиций без учета стоимости доставки и скидок, примененных к заказу */
		BigDecimal overallCleanAmount = new BigDecimal(0);
		/* стоимость доставки всех согласованных позиций заказа */
		BigDecimal overallDeliveryCost = new BigDecimal(0);

		for (OrderPosition orderPosition : participatedInPaymentOrderPositions) {
			Order thisItemEffectiveOrder = orderPosition.getProductItem().getEffectiveOrder();
			boolean itemBelongsToThisOrder =
					thisItemEffectiveOrder != null && thisItemEffectiveOrder.getId().equals(id);

			boolean itemBelongsToThisOrderAndWaitsForSaleResolution =
					itemBelongsToThisOrder
							&& orderPosition.getProductItem().getState() == ProductItem.State.PURCHASE_REQUEST;

			boolean itemBelongsToThisOrderAndConfirmedForSale =
					itemBelongsToThisOrder
							&& orderPosition.getProductItem().getState() == ProductItem.State.SALE_CONFIRMED;

			if (itemBelongsToThisOrderAndWaitsForSaleResolution) {
				isCompleted = false;
			}

			if (itemBelongsToThisOrderAndConfirmedForSale) {
				overallCleanAmount = overallCleanAmount.add(orderPosition.getAmount());
				overallDeliveryCost = overallDeliveryCost.add(
						orderPosition.getDeliveryCost() == null? ZERO
								: orderPosition.getDeliveryCost());

				atLeastOneItemIsConfirmedForSale = true;
			}
		}

		/* сумма, которая списывается в итоге со счета клиента */
		BigDecimal actualAmountToPay;
		if (promoCode == null && giftCard == null) {
			actualAmountToPay = overallCleanAmount.add(overallDeliveryCost);
		} /* доставка в скидку не входит */
		else if (promoCode != null) {
			actualAmountToPay = promoCode.getPriceWithDiscount(overallCleanAmount).add(overallDeliveryCost);
		}
		else {
			actualAmountToPay = giftCard.getPriceWithDiscount(overallCleanAmount).add(overallDeliveryCost);
		}

		return new OrderCompletionReadiness(
				isCompleted,
				!atLeastOneItemIsConfirmedForSale,
				actualAmountToPay);
	}

	/** Получить позицию заказа, которая содержит данную вещь */
	public Optional<OrderPosition> getPosition(ProductItem itemWhoseSaleIsResolved) {

	    return orderPositions.stream()
                .filter(p -> p.getProductItem().getId().equals(itemWhoseSaleIsResolved.getId()))
                .findFirst();
	}

	/**
	 * @return данные о примененной к заказу скидке, если такая есть.
	 */
	public Optional<Discount> getDiscount() {

		if (promoCode == null && giftCard == null) { return Optional.empty(); }

		if (promoCode != null) {
			Discount discount = new Discount("Промо-код",
					promoCode.getCode(),
					!promoCode.isExpired(),
					promoCode.getFormattedValue(),

                    Utils.prettyRoundToCents(promoCode.getSavingsValue(getCleanAmount())),

					Utils.prettyRoundToCents(getAmountWithDiscount()),

					Utils.prettyRoundToCents(getAmountWithDiscount().add(getDeliveryCost()))
			);

			return Optional.of(discount);
		}

		/* Если сумма сертификата меньше стоимости заказа, то
		* показывать экономию смысла нет: она будет равна сумме самого сертифитка.
		* В том случае, если сертификат дороже всего заказа, то пользователь
		* экономит не на сумму сертификата, а на сумму заказа. Ее и надо показать.
		* */
		BigDecimal savingsIfAny = giftCard.getAmount().compareTo(getCleanAmount()) > 0?
				getCleanAmount()
				: null;

        Discount discount = new Discount("Подарочный сертификат",
                giftCard.getCode(),
                !giftCard.isExpired(),
                String.format("%s ₽", giftCard.getAmount()),

                savingsIfAny != null? Utils.prettyRoundToCents(savingsIfAny) : null,

				Utils.prettyRoundToCents(getAmountWithDiscount()),

				Utils.prettyRoundToCents(getAmountWithDiscount().add(getDeliveryCost()))
        );

		return Optional.of(discount);
	}

	public BigDecimal getPriceWithDiscount(BigDecimal price) {
		if (promoCode == null && giftCard == null) {
			return price;
		}

		return  promoCode != null?
				promoCode.getPriceWithDiscount(price)
				: giftCard.getPriceWithDiscount(price);
	}

	/**
	 * <p>
	 *     Получить данные о применненной скидки. Размер скидки вычисляется с учетом
	 * стоимости не всех позиций заказа, а только тех, которые доступны для покупки
	 * на данный момент.
	 * </p>
	 * <p>
	 *    Так как сам заказ не обладает информацией о таких позициях, то они
	 * передаются ему в качестве аргумента метода.
	 * </p>
	 *
	 * @return данные о примененной к заказу скидке с учетом только доступных для покупки позиций.
	 */
	public Optional<Discount> getDiscount(List<OrderPosition> availablePositions) {

		if (promoCode == null && giftCard == null) { return Optional.empty(); }

		BigDecimal cleanAmount = availablePositions.stream().
				map(OrderPosition::getAmount)
				.reduce(ZERO, BigDecimal::add);

		BigDecimal amountWithDiscount
				= promoCode == null && giftCard == null? cleanAmount
				: promoCode != null? promoCode.getPriceWithDiscount(cleanAmount)
				: giftCard.getPriceWithDiscount(cleanAmount);

		BigDecimal deliveryCost = availablePositions.stream()
				.map(OrderPosition::getDeliveryCost)
				.filter(Objects::nonNull)
				.reduce(ZERO, BigDecimal::add);

		if (promoCode != null) {
			Discount discount = new Discount("Промо-код",
					promoCode.getCode(),
					!promoCode.isExpired(),
					promoCode.getFormattedValue(),

					/* Если у заказа нет ни одной доступной позиции,
					не выводить данные о стоимости заказа с учетом скидки
					и сумму, которую экономит покупатель: заказ, к котором нет доступных позиций, оплатить нельзя. */

					availablePositions.isEmpty()? null : Utils.prettyRoundToCents(promoCode.getSavingsValue(cleanAmount)),

					availablePositions.isEmpty()? null :Utils.prettyRoundToCents(amountWithDiscount),

					availablePositions.isEmpty()? null :Utils.prettyRoundToCents(amountWithDiscount.add(deliveryCost))
			);

			return Optional.of(discount);
		}

		/* Если сумма сертификата меньше стоимости заказа, то
		* показывать экономию смысла нет: она будет равна сумме самого сертифитка.
		* В том случае, если сертификат дороже всего заказа, то пользователь
		* экономит не на сумму сертификата, а на сумму заказа. Ее и надо показать.
		* */
		BigDecimal savingsIfAny = giftCard.getAmount().compareTo(cleanAmount) > 0?
				cleanAmount
				: null;

		Discount discount = new Discount("Подарочный сертификат",
				giftCard.getCode(),
				!giftCard.isExpired(),
				String.format("%s ₽", giftCard.getAmount()),

				savingsIfAny != null? Utils.prettyRoundToCents(savingsIfAny) : null,

				Utils.prettyRoundToCents(amountWithDiscount),

				Utils.prettyRoundToCents(amountWithDiscount.add(deliveryCost))
		);

		return Optional.of(discount);
	}

	/** Применить ранее установленную скидку к стоимости заказа.
	 * Работает только при наличии позиций, товары в которых доступны для покупки.
	 * Такие позиции помечаются в начале холда флагом {@link OrderPosition#participatesInPayment}
	 * */
	public void applyDiscount() {

		if (promoCode == null && giftCard == null) { return; }

		BigDecimal cleanAmount = getCleanAmount();
		if (promoCode != null) {
			amount = promoCode.getPriceWithDiscount(cleanAmount)
					.add(getDeliveryCost());
		}
		else {
			amount = giftCard.getPriceWithDiscount(cleanAmount)
					.add(getDeliveryCost());
		}
	}

	/**
     * Отменить назначенную ранее на этот заказ скидку.
	 * @throws OrderException если скидку отменить нельзя, с текстом причины.
	 */
	public void dropDiscount() throws OrderException {

		if (state != OrderState.CREATED && state != OrderState.REFUND) {
			throw new OrderException("Нельзя отменить скидку для заказа, который уже оплачен или находится в процессе оплаты");
		}

		promoCode = null;

		if (giftCard != null) {
			giftCard.setOrder(null);
			giftCard = null;
		}
	}

	public void addPosition(OrderPosition p) {
		orderPositions.add(p);
		p.setOrder(this);
	}

	public void removePosition(OrderPosition p) {
		orderPositions.remove(p);
		p.setOrder(null);
	}

	/** Начальная стоимость заказа с учетом скидки, если таковая применена к заказу.
	 * Скидка <b>не</b> применяется к стоимости доставки, а только к стоимости позиций заказа. */
	private BigDecimal getAmountWithDiscount() {

		BigDecimal cleanAmount = getCleanAmount();

		//noinspection ConstantConditions
		if (promoCode == null && giftCard == null) {
			return cleanAmount;
		}
		else if (promoCode != null ) {
			return promoCode.getPriceWithDiscount(cleanAmount);
		}
		else {
			return giftCard.getPriceWithDiscount(cleanAmount);
		}
	}

	/** Начальная стоимость заказа без учетка стоимости доставки и примененных скидок */
	private BigDecimal getCleanAmount() {

		return participatedInPaymentOrderPositions.stream().
				map(OrderPosition::getAmount)
				.reduce(ZERO, BigDecimal::add);
	}

	/** @apiNote костыль */
	public void applyDiscount(List<OrderPosition> participatedInPaymentOrderPositions) {

		if (promoCode == null && giftCard == null) { return; }

		BigDecimal cleanAmount = participatedInPaymentOrderPositions.stream()
				.map(OrderPosition::getAmount)
				.reduce(ZERO, BigDecimal::add);

		if (promoCode != null) {
			amount = promoCode.getPriceWithDiscount(cleanAmount)
					.add(getDeliveryCost());
		}
		else {
			amount = giftCard.getPriceWithDiscount(cleanAmount)
					.add(getDeliveryCost());
		}
	}
}
