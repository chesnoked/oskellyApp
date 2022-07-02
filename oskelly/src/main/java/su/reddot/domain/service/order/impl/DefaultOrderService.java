package su.reddot.domain.service.order.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.querydsl.QSort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.dao.order.OrderRepository;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.logistic.DestinationType;
import su.reddot.domain.model.logistic.event.SaleConfirmedEvent;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.notification.OrderStateChangedNotification;
import su.reddot.domain.model.notification.SaleNeedConfirmationNotification;
import su.reddot.domain.model.order.*;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.product.event.ProductItemSaleResolved;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.attractionOfTraffic.AttractionOfTrafficEvent;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.discount.DiscountService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.order.Discount;
import su.reddot.domain.service.order.OrderPositionStateChange;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.order.view.OrderItem;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.product.ItemStateChange;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.infrastructure.acquirer.Acquirer;
import su.reddot.infrastructure.acquirer.TransactionHandler;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionInfo;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionStatus;
import su.reddot.infrastructure.cashregister.CashRegister;
import su.reddot.infrastructure.cashregister.Checkable;
import su.reddot.infrastructure.cashregister.impl.starrys.type.BuyerCheck;
import su.reddot.infrastructure.sender.NotificationSender;
import su.reddot.presentation.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static su.reddot.domain.model.order.QOrder.order;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService, TransactionHandler {

	private final ProductItemService productItemService;
	private final ImageService       imageService;
	private final CommissionService  commissionService;
	private final DiscountService    discountService;
	private       ProductService     productService;

    private final OrderRepository         orderRepository;
    private final OrderPositionRepository orderPositionRepository;

	private final CashRegister              cashRegister;
	private final ApplicationEventPublisher publisher;
	private final ObjectMapper              objectMapper;
	private final NotificationSender        sender;


	// циклическая зависимость CartService <-> OrderService
	// рассмотреть вариант отправки события cartService'у вместо прямого вызова метода этого сервиса
//	private final ShoppingCartService cartService;

	private final Acquirer       acquirer;
	private final TemplateEngine templateEngine;

	@Value("${app.host}")
	private String appHost;

	@PostConstruct
	public void registerItselfAsTransactionHandler() {
		acquirer.registerTransactionHandler(this);
	}

	@Autowired
	public void setProductService(@Lazy ProductService ps) {
		productService = ps;
	}

	@Transactional
	@Override
	public Order createOrder(User user, Set<ProductItem> productItems) {

	    /*
	     * В заказ не должны попасть дубликаты одной и той же вещи.
	     * БД не контролирует уникальность вещи в пределах заказа (почему?)
	     * Уникальность обеспечивается через применения set'а, что возможно
	     * не является лучшим решением
	     */
		if (productItems.isEmpty()) {
			throw new OrderCreationException("Заказ не может быть пустым");
		}

		/*
		 * Вещи не бронировать.
		 * Статус вещей может поменяться на такой, который
		 * отменяет возможность их покупки. Продавец может удалить вещь.
		 * Все эти проблемы будет решать модератор после получения успешной оплаты заказа.
		 */
		return createOrderFrom(new ArrayList<>(productItems), user);
	}

	@Override
	public Optional<OrderView> getUserOrder(Long orderId, User u) {
		Optional<Order> order = orderRepository.findByIdAndBuyer(orderId, u);
		if (!order.isPresent()) { return Optional.empty(); }

		OrderView cookedOrder = from(order.get());
		return Optional.of(cookedOrder);
	}

	@Override
	public List<OrderView> getOrders(User u) {
	    return getOrders(u, null);
	}

	@Override
	public List<OrderView> getOrders(User u, List<OrderState> interestingStates) {

		BooleanExpression predicate = order.buyer.eq(u);
		if (interestingStates != null && !interestingStates.isEmpty()) {
			predicate = predicate.and(order.state.in(interestingStates));
		}

		Iterable<Order> orders = orderRepository.findAll(predicate,
				new QSort(order.createTime.desc()));

		return StreamSupport.stream(orders.spliterator(), false)
				.map(this::from).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public String initHold(Long id, User user) {

		Optional<Order> orderToPayForIfAny = orderRepository.findByIdAndBuyer(id, user);

		Order orderToPayFor = orderToPayForIfAny.orElseThrow(() -> new IllegalArgumentException(
				"Заказ с идентификатором " + id + " не найден для пользователя " + user.getId()));

		if (!orderToPayFor.isReadyForPayment()) {
			throw new IllegalArgumentException( "Заказ с идентификатором " + id +
					" оплатить нельзя.");
		}

		orderToPayFor.getOrderPositions().forEach(position -> position.setParticipatesInPayment(true));
		orderToPayFor.applyDiscount();

		// порядок важен (getPaymentRequest может выкинуть исключение)
		String paymentRequest = acquirer.getPaymentRequest(orderToPayFor, "/orders/" + id, ZonedDateTime.now());

		orderToPayFor.setAsInPaymentProcess();

		return paymentRequest;
	}

	@Override
    @Transactional
	public String initHold(OrderToPayFor order, User user) {

		Long orderId = order.getId();
		Optional<Order> orderToPayForIfAny = orderRepository.findByIdAndBuyer(orderId, user);

		Order orderToPayFor = orderToPayForIfAny.orElseThrow(() -> new IllegalArgumentException(
				"Заказ с идентификатором " + orderId + " не найден для пользователя " + user.getId()));

		if (!orderToPayFor.isReadyForPayment()) {
			throw new IllegalArgumentException("Заказ с идентификатором " + orderId +
					" оплатить нельзя.");
		}

		/* Только для нового заказа проверять доступность товаров в его позициях */
		if (orderToPayFor.getState() == OrderState.CREATED) {

			ensureForPositionsAvailabilityOrDie(order, orderToPayFor);

			orderToPayFor.applyDiscount(orderPositionRepository.findAll(order.getAvailablePositions()));
		}
		else {
			orderToPayFor.applyDiscount();
		}

		// порядок важен (getPaymentRequest может выкинуть исключение)
		String paymentRequest = acquirer.getPaymentRequest(orderToPayFor, "/orders/" + orderId, ZonedDateTime.now());

		orderToPayFor.setAsInPaymentProcess();

		return paymentRequest;
	}

	@Override
	@Transactional
	public void setDeliveryRequisite(Long orderId, DeliveryRequisite newRequisite, User u) {

		Optional<Order> orderIfAny = orderRepository.findByIdAndBuyer(orderId, u);
		if (!orderIfAny.isPresent()) { return; }

		Order order = orderIfAny.get();
		DeliveryRequisite oldRequisite = order.getDeliveryRequisite();
		if (oldRequisite != null && !newRequisite.complete()) { return; }

		if (newRequisite.getDeliveryExtensiveAddress() == null && oldRequisite != null) {
			newRequisite.setDeliveryExtensiveAddress(oldRequisite.getDeliveryExtensiveAddress());
		}

		order.setDeliveryRequisite(newRequisite);

		orderRepository.save(order);
	}

	@Override
	@Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleSaleResolution(ProductItemSaleResolved event) {

		ProductItem resolvedItem = productItemService.findById(event.getResolvedItemId()).orElseThrow(
				() -> new IllegalStateException("Вещь не найдена: " + event.getResolvedItemId())
		);

		Order effectiveOrder = resolvedItem.getEffectiveOrder();
		if (resolvedItem.getState() == ProductItem.State.SALE_CONFIRMED) {
			OrderPosition correspondingPosition = effectiveOrder.getPosition(resolvedItem).orElseThrow(
					() -> new IllegalStateException("У оплаченной вещи нет активного заказа. Вещь: "
							+ resolvedItem.getId()) );

			publisher.publishEvent(new SaleConfirmedEvent(correspondingPosition.getId(),
					DestinationType.SELLER, DestinationType.OFFICE));
		}

		OrderCompletionReadiness completionResult = effectiveOrder.getCompletionReadiness();
		if (completionResult.isCompleted() && !completionResult.isEntirelyUnavailable()) {

			effectiveOrder.setEffectiveAmount(completionResult.getActualAmountToPay());

			acquirer.completeHold(Integer.valueOf(effectiveOrder.getTransactionId()),
					effectiveOrder.getId(),
                    completionResult.getActualAmountToPay(),
                    ZonedDateTime.now());

			effectiveOrder.setState(OrderState.HOLD_COMPLETED);

            String buyerCheck = null;
            try {
                buyerCheck = cashRegister.requestCheck(effectiveOrder);
                BuyerCheck check = objectMapper.readValue(buyerCheck, BuyerCheck.class);
                if (check.hasErrors()) {
                    log.error("Чек заказа содержит ошибки, отправка невозможна. Заказ: {}",
                            effectiveOrder.getId());
                }
                else {
                    formatAndSend(check, effectiveOrder);
                }
            }
            catch (Exception e) { log.error(e.getMessage(), e); }

				effectiveOrder.setBuyerCheck(buyerCheck);
				orderRepository.save(effectiveOrder);
		} else //noinspection StatementWithEmptyBody
		if (completionResult.isCompleted() && completionResult.isEntirelyUnavailable()) {

            acquirer.refund(Integer.valueOf(effectiveOrder.getTransactionId()), effectiveOrder.getId(),
                    effectiveOrder.getAmount(), ZonedDateTime.now());

            effectiveOrder.setState(OrderState.REFUND);

            try { // вернуть скидку покупателю для ее повторного использования
                effectiveOrder.dropDiscount();
            } catch (OrderException e) {
            	// не должно возникать, так как заказа уже перевели в состояние REFUND,
				// которое позволяет отменить скидку.
                log.error("{}, заказ: ", e.getMessage(), effectiveOrder.getId(), e);
            }
            orderRepository.save(effectiveOrder);
		}
		else { /* заказ еще не готов к допроведению платежа */ }

	}

	@Override
	public Optional<OrderPosition> findOrderPositionWithGivenItem(Order order, ProductItem item) {
		return orderPositionRepository.findByOrderAndProductItem(order, item);
	}

    @Override
    @Transactional
	public void handleTransaction(TransactionInfo t) {

	    Long orderIdAsInTransaction;
		try {
			orderIdAsInTransaction = Long.valueOf(t.getOrderId());
		} catch (NumberFormatException e) {
			/* идентификатор заказа в транзакции не обязательно является идентификатором заказа в системе,
			 * а может быть идентификатором другой оплачиваемой сущности, например, подарочного сертификата. */
			return;
		}

		Order order = orderRepository.findOne(orderIdAsInTransaction);
		if (order == null) {
			log.error("Получена транзакция на заказ с ID {}, которого нет в системе.",
					t.getOrderId());
			return;
		}

		TransactionStatus tranStatus = t.getStatus().getType();

		if (tranStatus == TransactionStatus.hold_wait) {
			handleHoldTransaction(order, t.getId());
		}
		else if (tranStatus == TransactionStatus.error) {
			handleErrorTransaction(order, t.getOriginalTransactionId());
		}
		else {
			// TODO обрабатывать все типы транзкаций
			log.error("Транзакция, которая получена от банка, не обработана: нет обработчика для такого типа транзакции. " +
					"Содержимое транзакции: {}", t.toString());
			throw new NotImplementedException();
		}
	}

	@Override
	public void saveOrderPosition(OrderPosition orderPosition) { orderPositionRepository.save(orderPosition); }

	@Override
    @Transactional
	public Discount addDiscount(Long id, User buyer, String code)
			throws NotFoundException, DiscountIsAlreadyUsedException, OrderException {

		Order order = orderRepository.findByIdAndBuyer(id, buyer).orElseThrow(
				() -> new NotFoundException("Заказ с номером: " + id + " не найден")
		);

		return addDiscount(code, order);
	}

	@Override
    @Transactional
	public Discount addDiscount(String code, Order order)
			throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {

	    BigDecimal price = null;
		if (order.isPayable()) {
			List<OrderPosition> availablePositions = order.getOrderPositions().stream()
					.filter(isAvailable())
					.collect(Collectors.toList());

			if (availablePositions.size() > 0) {
				price = availablePositions.stream()
						.map(p -> p.getProductItem().getCurrentPrice())
						.reduce(BigDecimal.ZERO, BigDecimal::add);
			}
            /* Еслиз заказ - новый, и так получилось, что в нем нет доступных товаров,
            * то применять скидку к нему не имеет никакого смысла. */
			else {
				throw new IllegalArgumentException("Нельзя применить скидку к пустому заказу");
			}
		}

		Optional<PromoCode> promoCodeIfAny = discountService.findPromoCode(code);
		if (promoCodeIfAny.isPresent() && promoCodeIfAny.get().supports(price)) {
			PromoCode existingPromoCode = promoCodeIfAny.get();

			/* аноним может назначать скидку для корзины */
			User buyer = order.getBuyer();
			if (buyer != null) {
				Order anotherOrderWithGivenPromoCode
						= orderRepository.findOne(QOrder.order.promoCode.eq(existingPromoCode)
						.and(QOrder.order.buyer.eq(buyer))
                        /* один и тот же промокод можно добавить одновременно в несколько заказов,
                        * только если они еще не оплачены. */
						.and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
								OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED )));

				if (anotherOrderWithGivenPromoCode != null) {
					throw new DiscountIsAlreadyUsedException(anotherOrderWithGivenPromoCode);
				}
			}

			order.setDiscount(existingPromoCode);
		}
		else {
			GiftCard giftCard = discountService.findGiftCard(code).orElseThrow(
					() -> new NotFoundException("Скидка c кодом: " + code + " не найдена"));

				Order anotherOrderWithThisGiftCard
						= orderRepository.findOne(QOrder.order.giftCard.id.eq(giftCard.getId())
                        /* один и тот же подарочный сертификат можно добавить одновременно в несколько заказов,
                        * до тех пор пока не появится любой другой заказ, который уже оплачен этим сертификатом. */
						.and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
								OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED)));
				if (anotherOrderWithThisGiftCard != null) {
					throw new DiscountIsAlreadyUsedException(anotherOrderWithThisGiftCard);
				}

			order.setDiscount(giftCard);
        }

		return order.getDiscount().orElseThrow(IllegalStateException::new);
	}

	@Override
	public void removeDiscount(Long id, User buyer) throws OrderException, NotFoundException {
	   	Order order = orderRepository.findByIdAndBuyer(id, buyer).orElseThrow(
				() -> new OrderException("Заказ с номером: " + id + " не найден")
		);

		order.dropDiscount();
		orderRepository.save(order);
	}

	/**
	 * История движения позиции заказа. История доступна только для тех позиций,
	 * которые оплатил покупатель.
	 */
	private List<OrderPositionStateChange> getStateHistory(OrderPosition orderPosition) {

		boolean positionIsNotInPayedOrder = orderPosition.getParticipatesInPayment() == null
			|| orderPosition.getParticipatesInPayment().equals(Boolean.FALSE)
			|| !orderPosition.getState().isPresent()
			|| orderPosition.getState().get() == OrderPosition.State.UNAVAILABLE;

		if (positionIsNotInPayedOrder) { return Collections.emptyList(); }

		List<ItemStateChange> itemStateHistory = productService.getStateHistory(orderPosition.getProductItem());

		return itemStateHistory.stream()

                /* время появления вещи в системе не должно отображаться
                в истории позиции заказа */
				.filter(h -> h.getState() != ProductItem.State.INITIAL)

				.map(h -> new OrderPositionStateChange().setAt(h.getAt())
						.setState(orderPosition.from(h.getState())
								.orElseThrow(() -> new IllegalStateException("Неизвестное состояние: "
										+ h.getState().toString()))))
				.collect(Collectors.toList());
	}

	/**
	 * Обработать сценарий успешного удержания средств у пользователя при оплате заказа
	 * @param order успешно оплаченный заказ
	 * @param transactionId идентификатор транзакции на стороне банка
	 */
	private void handleHoldTransaction(Order order, int transactionId) {

		/* Отменить заказ, если уже есть оплаченный заказ с такой же скидкой. */
		PromoCode pc = order.getPromoCode();
		GiftCard  gc = order.getGiftCard();
		if (pc != null || gc != null) {
			if (pc != null) {
				Order another = orderRepository.findOne(
						QOrder.order.promoCode.eq(pc)
						.and(QOrder.order.ne(order))
                        .and(QOrder.order.buyer.eq(order.getBuyer()))
						.and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
								OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED)));

				if (another != null) {
					acquirer.refund(transactionId, order.getId(), order.getAmount(), ZonedDateTime.now());
					order.setState(OrderState.REFUND);
					return;
				}
			}
			else {
				Order another = orderRepository.findOne(
						QOrder.order.giftCard.id.eq(gc.getId())
                        .and(QOrder.order.ne(order))
                        .and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
                                OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED)));

				if (another != null) {
					acquirer.refund(transactionId, order.getId(), order.getAmount(), ZonedDateTime.now());
					order.setState(OrderState.REFUND);
					return;
				}
			}
		}

		List<OrderPosition> orderItems = order.getOrderPositions();

		int notAvailableProductItems = 0;
		for (OrderPosition orderItem : orderItems) {
			ProductItem orderedProductItem = orderItem.getProductItem();

			Optional<BigDecimal> negotiatedPriceIfAny
					= productService.getNegotiatedPrice(orderedProductItem.getProduct(), order.getBuyer());
			boolean productItemHasNegotiatedPriceAndIsAvailable
					= negotiatedPriceIfAny.isPresent()
					&& negotiatedPriceIfAny.get().compareTo(orderItem.getAmount()) == 0
                    && orderedProductItem.isAvailable();

			Optional<ProductItem> productItemToSellIfAny
					= productItemService.getItemForSale(orderedProductItem, orderItem.getAmount());

			if (productItemToSellIfAny.isPresent() || productItemHasNegotiatedPriceAndIsAvailable) {

				/* здесь позиция товара может поменять вещь, на которую она ссылается
				 * в случае, если вещи, на которую она изначально ссылалась, в продаже нет,
				 * но есть другая вещь подходящего размера и цены */
				ProductItem productItemToSell;
				if (productItemToSellIfAny.isPresent()) {
				    productItemToSell = productItemToSellIfAny.get();
					orderItem.setProductItem(productItemToSell);
				}
				else {
					productItemToSell = orderedProductItem;
				}

			    productItemToSell.setState(ProductItem.State.PURCHASE_REQUEST);
				productItemToSell.setEffectiveOrder(order);
				try {
					productItemService.save(productItemToSell);
				} catch (CommissionException e) {

					// TODO неясно что делать в этом случае
					/* когда оплата на вещи в заказе прилетела,
					* а система не может обновить состояние этих вещей
					* (проставить действительный заказ у вещи) */
					log.error(e.getMessage(), e);
				}

				publisher.publishEvent(
						new SaleNeedConfirmationNotification()
								.setProductItem(productItemToSell)
								.setUser(productItemToSell.getProduct().getSeller())
				);

				sendSaleConfirmationRequestToSeller(productItemToSell);
			}
			else {
				++notAvailableProductItems;
			}
		}

		/* Сразу вернуть клиенту всю сумму заказа, если ни одну вещь в данный момент приобрести нельзя */
		if (orderItems.size() == notAvailableProductItems) {
		    // TODO обработка результата операции
			acquirer.refund(transactionId, order.getId(), order.getAmount(), ZonedDateTime.now());
			order.setState(OrderState.REFUND);

			try { // вернуть скидку покупателю для ее повторного использования
				order.dropDiscount();
			} catch (OrderException e) {
				log.error("{}, заказ: ", e.getMessage(), order.getId(), e);
			}
		}
		else {
			order.setAsPaid(Integer.toString(transactionId));
			Notification notification = new OrderStateChangedNotification()
					.setNewOrderState(order.getState())
					.setOrder(order)
                    .setUser(order.getBuyer());

			publisher.publishEvent(notification);
		}

		orderRepository.save(order);
		publisher.publishEvent(new AttractionOfTrafficEvent(order));
	}

	private void sendSaleConfirmationRequestToSeller(ProductItem productItemToSell) {

        Product productToSell = productItemToSell.getProduct();

        Context ctx = new Context();
        ctx.setVariable("resource",
                String.format("%s/account/products/items/%d/sale-confirmation", appHost, productItemToSell.getId()));

        ctx.setVariable("name", productToSell.getBrand().getName() + " " + productToSell.getDisplayName());

        String messageContent = templateEngine.process("mail/sale-confirmation", ctx);

		sender.send(productToSell.getSeller().getEmail(),
        		"Подтвердите продажу товара " + productToSell.getId(),
				messageContent);
	}

	/**
	 * Обработать сценарий неудачной оплаты заказа
	 * @param order заказ
	 * @param transactionId идентификатор транзакции на стороне банка
	 */
	private void handleErrorTransaction(Order order, int transactionId) {
	    order.setTransactionId(String.valueOf(transactionId));
		order.setAsFailed();
	}

	/* Формирует представление заказа для его отображения клиенту */
	private OrderView from(Order order) {
		OrderView orderView = new OrderView(
				order.getId(),
				order.getState().getDescription(),
				order.getState().name(),
				order.isPayable(),
				order.isReadyForPayment(),
				order.isPaidAtThisMoment(),
				order.getDeliveryRequisite(),
				order.getCreateTime()
				);

		/* Пользователь видит разный список позиций в зависимости от состояния заказа. */
		OrderState currentOrderState = order.getState();
		if (currentOrderState == OrderState.CREATED) {

			/* Если заказ еще не оплачивался, то список позиций будет меняться
			* каждый раз, когда пользователь его запрашивает.
			* Его содержимое будет зависеть от того, доступен ли товар позиции
			* или нет на момент запроса списка позиций. */


			BigDecimal orderAmountWithoutDeliveryCosts = BigDecimal.ZERO;
			BigDecimal deliveryCosts = BigDecimal.ZERO;

			int totalFaultyOrderPositions = 0;
			List<OrderPosition> orderPositions = order.getOrderPositions();
			for (OrderPosition orderPosition : orderPositions) {

				OrderItem orderItem = from(orderPosition);

				ProductItem p = orderPosition.getProductItem();
				boolean orderedProductItemIsAvailable = p.isAvailable()
						|| productItemService.getItemLikeThisThatCanBeOrdered(p).isPresent();

				if (orderedProductItemIsAvailable) {
					orderAmountWithoutDeliveryCosts = orderAmountWithoutDeliveryCosts.add(orderPosition.getAmount());
					orderItem.setAvailable(true);

					if (orderPosition.getDeliveryCost() != null) {
						deliveryCosts = deliveryCosts.add(orderPosition.getDeliveryCost());
					}
				}
				else {
					orderItem.setAvailable(false);
					++totalFaultyOrderPositions;
				}

				orderView.getItems().add(orderItem);
			}

			orderView.setPrice(orderAmountWithoutDeliveryCosts);
			orderView.setDeliveryCost(deliveryCosts);

			if (totalFaultyOrderPositions == orderPositions.size()) {
				orderView.setFaulty(true);
			}

            orderView.setAppliedDiscount(order.getDiscount(
                    orderPositions.stream()
                            .filter(isAvailable()).collect(Collectors.toList()))
                    .orElse(null));
		}
		else {
		    /* Если пользователь уже пытался оплатить заказ, то показывать только
		    * те позиции, которые на момент оплаты были доступны. Остальные - которые
		    * не попали на оплату, не показывать вообще. */

			BigDecimal orderAmountWithoutDeliveryCosts = BigDecimal.ZERO;

			for (OrderPosition orderPosition : order.getParticipatedInPaymentOrderPositions()) {
				OrderItem orderItem = from(orderPosition);
				orderAmountWithoutDeliveryCosts = orderAmountWithoutDeliveryCosts.add(orderPosition.getAmount());
				orderView.getItems().add(orderItem);
			}

			orderView.setPrice(orderAmountWithoutDeliveryCosts);
			orderView.setDeliveryCost(order.getDeliveryCost());
			orderView.setAppliedDiscount(order.getDiscount().orElse(null));
		}

		return orderView;
	}

	private OrderItem from(OrderPosition orderPosition) {
		ProductItem item = orderPosition.getProductItem();
		Product p = item.getProduct();
		Optional<String> imageUrl = imageService.getPrimaryImage(p).map(ProductImage::getUrl);

		OrderItem cooked = new OrderItem(
				orderPosition.getId(),
				p.getId(),
				item.getId(),
				imageUrl.orElse(null),
				p.getBrand().getName(),
				p.getDisplayName(),
				orderPosition.getAmount().setScale(2, RoundingMode.UP),
				item.getConcreteSizePretty().orElse(null),
				p.getSeller().getNickname(),
				p.getSeller().getId(),
				orderPosition.getState().map(OrderPosition.State::getDescription).orElse(null),
				Optional.ofNullable(orderPosition.getDeliveryCost()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.UP)
		);

		cooked.setAllStates(getStateHistory(orderPosition).stream()

				/* от новых состояний к старым */
				.sorted(Comparator.comparing(OrderPositionStateChange::getAt).reversed())

				.map(state -> new OrderItem.State()
						.setAt(state.getAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
						.setName(state.getState().getDescription()))

				.collect(Collectors.toList()));

		return cooked;
	}

	private Order createOrderFrom(List<ProductItem> productItems, User buyer) {

		Order order = new Order();
		order.setBuyer(buyer);
		order.setState(OrderState.CREATED);
		order.setUuid(UUID.randomUUID());
		order.setDeliveryRequisite(buyer.getDeliveryRequisite());

		BigDecimal totalOrderAmount = BigDecimal.ZERO;
		for (ProductItem productItem : productItems) {
			OrderPosition orderPosition = new OrderPosition();

			/* Стоимось позиции.
			Учитывать согласованную при торге цену на товар. */
			Product product = productItem.getProduct();
			BigDecimal productPrice = getNegotiationAwarePrice(productItem, buyer);
			orderPosition.setAmount(productPrice);
			orderPosition.setDeliveryCost(productItem.getNullableDeliveryCost());

			totalOrderAmount = totalOrderAmount.add(orderPosition.getEffectiveAmount());

			try {
				orderPosition.setCommission(commissionService.calculateCommission(productItem));
			} catch (CommissionException e) {
				log.error(e.getLocalizedMessage(), e);
				throw new OrderCreationException("Ошибка при расчете комиссии");
			}

			orderPosition.setOrder(order);
			orderPosition.setProductItem(productItem);
			orderPosition.setState(OrderPositionState.UNDEFINED);
			orderPosition.setStateTime(LocalDateTime.now());

			order.setOrderPosition(orderPosition);
		}

		order.setAmount(totalOrderAmount);
		orderRepository.save(order);

		return order;
	}

	private ProductItem reserveProductItem(Long productItemId, User buyer) throws CommissionException {

		Optional<ProductItem> productItem
				= productItemService.getForReservationWithLocking(productItemId);

		if (!productItem.isPresent()) {

			Optional<Product> existingProduct = productService.getRawProduct(productItemId);
			if (!existingProduct.isPresent()) {
				log.error("Попытка создать заказ из товара, которого не существует в системе. Пользователь: {}, товар: {}",
						buyer.getId(), productItemId);
				throw new OrderCreationException(String.format(
						"Нельзя добавить в заказ несуществующий товар. Удалите товар с id <%d> и повторите оформление заказа",
						productItemId));
			} else {
				throw new OrderCreationException(
						String.format("Товар <%s %s> временно заблокирован. Удалите товар и повторите оформление.",
						existingProduct.get().getBrand().getName(), existingProduct.get().getName()));
			}
		}

		ProductItem productItemToOrder = productItem.get();

		if (productItemToOrder.getProduct().getProductState() != ProductState.PUBLISHED) {
			throw new OrderCreationException(String.format(
					"Товар <%s %s> снят с продажи. Удалите товар из заказа и повторите оформление"
					, productItemToOrder.getProduct().getBrand().getName(), productItemToOrder.getProduct().getName()));
		}

		// todo: подумать над временем блокировки
		productItemToOrder.setReserveExpireTime(LocalDateTime.now().plusMinutes(15));

		// todo: сделать сервис, снимающий блокировки товара
		productItemToOrder.setReserver(buyer);
		productItemToOrder.setReserveType(ReserveType.IMPLICIT);
		productItemService.save(productItemToOrder);

		return productItemToOrder;
	}


	private Predicate<OrderPosition> isAvailable() {
		return orderPosition -> orderPosition.getProductItem().isAvailable()
				|| productItemService.getItemLikeThisThatCanBeOrdered(
						orderPosition.getProductItem()).isPresent();
	}

	private Predicate<OrderPosition> isInvalid(Order o) {

		return orderPosition -> orderPosition == null
                || !orderPosition.getOrder().getId().equals(o.getId())
                || !(orderPosition.getProductItem().isAvailable()
                || productItemService.getItemLikeThisThatCanBeOrdered(
                		orderPosition.getProductItem()).isPresent());
    }

	private Predicate<OrderPosition> isValid(Order o) {

		return orderPosition -> orderPosition == null
				|| !orderPosition.getOrder().getId().equals(o.getId())
				|| (orderPosition.getProductItem().isAvailable()
				|| productItemService.getItemLikeThisThatCanBeOrdered(orderPosition.getProductItem()).isPresent());
	}

	private void ensureForPositionsAvailabilityOrDie(OrderToPayFor order, Order actualOrder) {

		if (order.getAvailablePositions() == null || order.getUnavailablePositions() == null) {
			throw new IllegalArgumentException("Для оплаты нового заказа необходимо передать его позиции.");
		}

		boolean availableEarlierPositionBecameUnavailable = order.getAvailablePositions().stream()
				.map(orderPositionRepository::findOne)
				.anyMatch(isInvalid(actualOrder));

		if (availableEarlierPositionBecameUnavailable) {
			throw new IllegalArgumentException("Некоторые позиции поменяли свое состояние");
		}

		boolean unavailableEarlierPositionsBecameAvailable = order.getUnavailablePositions().stream()
				.map(orderPositionRepository::findOne)
				.anyMatch(isValid(actualOrder));

		if (unavailableEarlierPositionsBecameAvailable) {
			throw new IllegalArgumentException("Некоторые позиции поменяли свое состояние");
		}

			/* У пользователя резервируются средства только за доступные на данный момент товары.
			 * Для этого нужно отдельно отметить позиции с доступными товарами.
			 */
		// batch update mb?
		order.getAvailablePositions().stream()
				.map(orderPositionRepository::findOne).forEach(op -> op.setParticipatesInPayment(true));
		order.getUnavailablePositions().stream()
				.map(orderPositionRepository::findOne).forEach(op -> op.setParticipatesInPayment(false));
	}

	private void formatAndSend(BuyerCheck check, Order order) {

		String url = cashRegister.getQrUrl(check.getQrCode().get());

		Check cookedCheck = new Check()
				.setQrAbsoluteUrl(appHost + url)
				.setDetails(check);

		ArrayList<Line> lines = new ArrayList<>();
		order.getLinesWithPositions().forEach((position, line) -> lines.add(from(position, line)));
		cookedCheck.setLines(lines);

		Context ctx = new Context();
		ctx.setVariable("check",  cookedCheck);
		ctx.setVariable("host",  appHost);

		String messageContent = templateEngine.process("mail/check", ctx);

		sender.send(order.getBuyer().getEmail(), "Чек", messageContent);
	}

	private Line from(OrderPosition p, Checkable.Line l) {
		Product rawProduct = p.getProductItem().getProduct();

		Seller seller = new Seller()
				.setName(rawProduct.getSeller().getNickname())
				.setPro(rawProduct.getSeller().isPro());

		ProductLine productLine = new ProductLine()
				.setName(rawProduct.getDisplayName())
				.setBrand(rawProduct.getBrand().getName())
				.setCondition(rawProduct.getProductCondition().getName())
				.setAbsoluteImageUrl(appHost + productService.getPrimaryImage(rawProduct).get().getThumbnailUrl());

		Amount amount = new Amount()
				.setDeliveryPart(p.getDeliveryCost() == null ? "0" : Utils.prettyRoundToCents(p.getDeliveryCost()))
				.setProductPart(Utils.prettyRoundToCents(l.getPrice()))
				.setTotal(Utils.prettyRoundToCents(
						(p.getDeliveryCost() == null ? BigDecimal.ZERO : p.getDeliveryCost())
								.add(l.getPrice())));
		return new Line()
				.setSeller(seller)
				.setProduct(productLine)
				.setAmount(amount);
    }

	/** @return согласованная цена, если есть, или текущая цена вещи. */
	private BigDecimal getNegotiationAwarePrice(ProductItem item, User buyer) {

		BigDecimal negotiationAwarePrice = item.getCurrentPrice();
        /* аноним не имеет возможности торговаться с продавцом */
        Optional<BigDecimal> negotiatedPrice = productService.getNegotiatedPrice(
                item.getProduct(), buyer);

        boolean negotiatedPriceIsValid = negotiatedPrice.isPresent()
                && item.getCurrentPrice().compareTo(negotiatedPrice.get()) > -1;
        if (negotiatedPriceIsValid) {
            negotiationAwarePrice = negotiatedPrice.get();
        }

		return negotiationAwarePrice;
	}


	/** Данные о заказе как они отображаются в чеке.
	 * Просто данных чека для формирования письма с чеком недостаточно,
	 * так как в нем (чеке) отображаются дополнительно изображения купленных товаров, данные о продавце. */
	@Getter @Setter @Accessors(chain = true)
	static class Check {

		/** Абсолютная ссылка на изображение с qr кодом. */
		private String qrAbsoluteUrl;

		/** Позиции чека. */
		private List<Line> lines;

		/** Подробные данные чека. */
		private BuyerCheck details;
    }

    @Getter @Setter @Accessors(chain = true)
    static class Line {

		private ProductLine product;
		private Seller      seller;
		private Amount      amount;
	}

	@Getter @Setter @Accessors(chain = true)
	static class ProductLine {

		private String name;
		private String brand;
		private String condition;
		private String absoluteImageUrl;
	}

	@Getter @Setter @Accessors(chain = true)
	static class Seller {

		private String name;
		private boolean isPro;
	}

	/* Данные о стоимости в форматированном виде: */
	@Getter @Setter @Accessors(chain = true)
	static class Amount {
		private String deliveryPart;
		private String productPart;
		private String total;
	}
}

