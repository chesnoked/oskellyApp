package su.reddot.domain.service.cart.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.dao.order.OrderRepository;
import su.reddot.domain.dao.product.ProductItemRepository;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.order.*;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.product.QProductItem;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.cart.OrderCreationResult;
import su.reddot.domain.service.cart.ProductCartability;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.cart.impl.dto.Cart;
import su.reddot.domain.service.cart.impl.dto.GuestOrderCreationParams;
import su.reddot.domain.service.cart.impl.dto.Item;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.order.Discount;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.configuration.CustomAuthenticationSuccessEvent;
import su.reddot.infrastructure.security.SecurityService;
import su.reddot.infrastructure.sender.NotificationSender;
import su.reddot.infrastructure.util.ErrorNotification;
import su.reddot.presentation.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static su.reddot.domain.model.order.QOrder.order;
import static su.reddot.domain.model.order.QOrderPosition.orderPosition;
import static su.reddot.domain.model.product.QProductItem.productItem;

@Service
@AllArgsConstructor @Setter @Slf4j
public class DefaultCartService implements CartService<Cart> {

    private ProductItemService productItemService;
    private ProductService     productService;
    private ImageService       imageService;

    private OrderRepository   orderRepository;
    private OrderService      orderService;
    private CommissionService commissionService;

    private UserService                  userService;
    private SecurityService              securityService;

    private NotificationSender      sender;
    private TemplateEngine          templateEngine;

    private OrderPositionRepository positionRepository;
    private ProductItemRepository   itemRepository;

    /**
     * Добавить товар в корзину пользователя
     *
     * @param productId идентификатор добавляемого товара
     * @param u пользователь, в чью корзину нужно добавить товар
     *
     * @throws ProductNotFoundException товар, который пользователь пытается добавить в корзину,
     * физически не существует в системе
     *
     * @throws ProductCanNotBeAddedToCartException товар, который пользователь пытается добавить в корзину,
     * существует в системе, но его невозможно положить в корзину
     */
    @Override
    public Long addItem(Long productId, Long sizeId, User u)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException {

        ProductItem itemToAdd = getCartableItemOrDie(productId, sizeId, u, null);

        Order checkoutOrder = getCheckedOutOrder(u, null)
                .orElse(newCheckoutOrder(u, null));

        save(checkoutOrder, itemToAdd);

        return null;
    }

    @Override
    public Long addItem(Long productId, Long sizeId, String guest)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException {

        ProductItem itemToAdd = getCartableItemOrDie(productId, sizeId, null, guest);

        Order checkoutOrder = getCheckedOutOrder(null, guest)
                .orElse(newCheckoutOrder(null, guest));

        save(checkoutOrder, itemToAdd);

        return null;
    }


    @Override
    @Transactional
    public void removeItem(Long itemId, User u) {
        BooleanExpression cartItemToRemove = orderPosition.id.eq(itemId)
                .and(orderPosition.order.buyer.eq(u))
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        OrderPosition itemIfAny  = positionRepository.findOne(cartItemToRemove);
        if (itemIfAny == null) { return; }

        itemIfAny.getOrder().setState(OrderState.CREATED);
        positionRepository.delete(itemIfAny);
    }

    @Override
    @Transactional
    public void removeItem(Long itemId, String guest) {
        BooleanExpression cartItemToRemove = orderPosition.id.eq(itemId)
                .and(orderPosition.order.guestToken.eq(guest))
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        OrderPosition itemIfAny  = positionRepository.findOne(cartItemToRemove);
        if (itemIfAny == null) { return; }

        itemIfAny.getOrder().setState(OrderState.CREATED);
        positionRepository.delete(itemIfAny);
    }

    @Override
    public ProductCartability checkProductCartability(Product product, User u) {

        BooleanExpression predicate = orderPosition.productItem.product.eq(product)
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.buyer.eq(u))
                .and(orderPosition.order.amount.isNull());

        return check(product, predicate);
    }


    @Override
    public ProductCartability checkProductCartability(Product product, String guest) {

        BooleanExpression predicate = orderPosition.productItem.product.eq(product)
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.guestToken.eq(guest))
                .and(orderPosition.order.amount.isNull());

        return check(product, predicate);
    }

    /**
     * @return null если корзина пуста (когда в ней физически нет ни одного товара)
     */
    @Override
    @Transactional
    public Cart getCart(User user) {

        fixCartIfTheCase(user, null);

        BooleanExpression loggedInUserCartItemsPredicate
                = orderPosition.order.buyer.eq(user)
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        return getCart(loggedInUserCartItemsPredicate, user);
    }

    @Override
    @Transactional
    public Cart getCart(String guest) {

        fixCartIfTheCase(null, guest);

        BooleanExpression loggedInUserCartItemsPredicate
                = orderPosition.order.guestToken.eq(guest)
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        return getCart(loggedInUserCartItemsPredicate, null);
    }

    @Override
    public void addDiscount(String code, User user)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {

        Order checkedOutOrder = getCheckedOutOrder(user, null)
              .orElseThrow(() -> new IllegalArgumentException("Нельзя добавить скидку для пустой корзины"));

        orderService.addDiscount(code, checkedOutOrder);
    }

    @Override
    public void addDiscount(String code, String guest)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {

        Order checkedOutOrder = getCheckedOutOrder(null, guest)
              .orElseThrow(() -> new IllegalArgumentException("Нельзя добавить скидку для пустой корзины"));

        orderService.addDiscount(code, checkedOutOrder);
    }

    @Override
    @Transactional
    public void removeDiscount(User u) {

        getCheckedOutOrder(u, null)
            .ifPresent(order ->  {
                try {order.dropDiscount(); } catch (Exception ignored){}
            });
    }

    @Override
    @Transactional
    public void removeDiscount(String guest) {

        getCheckedOutOrder(null, guest)
                .ifPresent(order ->  {
                    try {order.dropDiscount(); } catch (Exception ignored){}
                });
    }

    @Override
    public OrderCreationResult createOrder(List<Long> itemIds, User loggedInUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public Order createGuestOrder(GuestOrderCreationParams params) {

        /* FIXME пенерести валидацию в отдельное место */
        User   loggedInUser = params.getLoggedInUser();
        String guestToken   = params.getGuestToken();

        if (loggedInUser == null
                && (params.getEmail() == null || params.getEmail().trim().isEmpty())) {
            throw new IllegalArgumentException("Не указана почта");
        }

        if (loggedInUser == null
                && (params.getNickname() == null || params.getNickname().trim().isEmpty())) {
            throw new IllegalArgumentException("Не указан псевдоним");
        }

        if (params.getName() == null || params.getName().trim().isEmpty()
                || params.getPhone() == null || params.getPhone().trim().isEmpty()
                || params.getCity() == null || params.getCity().trim().isEmpty()
                || params.getAddress() == null || params.getAddress().trim().isEmpty()
                || params.getZipCode() == null || params.getZipCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Не указаны обязательные параметры");
        }

        BooleanExpression cartPredicate = order.state.eq(OrderState.CREATED)
                .and(order.amount.isNull());

        BooleanExpression cartOwner = loggedInUser != null ?
                order.buyer.eq(loggedInUser)
                : order.guestToken.eq(guestToken);

        Order eligibleCart = orderRepository.findOne(cartPredicate.and(cartOwner));
        if (eligibleCart == null) {
            throw new IllegalArgumentException("Корзина не найдена");
        }

        List<OrderPosition> orderPositions = eligibleCart.getOrderPositions();
        if (orderPositions.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        String nullablePassword = null;
        if (loggedInUser == null) {
            UserService.UserWithPassword userWithPassword
                    = userService.registerSilently(params.getName().trim(),
                    params.getNickname().trim(),
                    params.getEmail().trim());

            securityService.loginByEmail(
                    params.getEmail(),
                    userWithPassword.getPassword(),
                    // для соблюдения контракта метода, хотя по факту не используется
                    new ErrorNotification());

            loggedInUser = userWithPassword.getUser();
            nullablePassword = userWithPassword.getPassword();
            eligibleCart.setBuyer(loggedInUser);
            eligibleCart.setGuestToken(null);
        }

        User finalLoggedInUser = loggedInUser;

        /* перед оплатой молча удаляем недоступные товары */
        List<OrderPosition> availablePositions = new ArrayList<>();
        for (Iterator<OrderPosition> i = orderPositions.iterator(); i.hasNext();) {
            OrderPosition position = i.next();
            if (productIsAvailable(position.getProductItem(), finalLoggedInUser)) {
                availablePositions.add(position);
            }
            else {
                i.remove();
                eligibleCart.removePosition(position);
            }
        }

        if (availablePositions.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        for (OrderPosition position : availablePositions) {

            ProductItem productItem = position.getProductItem();
			/* Стоимось позиции.
			Учитывать согласованную при торге цену на товар. */
            BigDecimal productPrice = getNegotiationAwarePrice(productItem, loggedInUser);
            position.setAmount(productPrice);
            position.setDeliveryCost(productItem.getNullableDeliveryCost());

            totalOrderAmount = totalOrderAmount.add(position.getEffectiveAmount());

            try {
                position.setCommission(commissionService.calculateCommission(productItem));
            } catch (CommissionException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new OrderCreationException("Ошибка при расчете комиссии");
            }

            position.setState(OrderPositionState.UNDEFINED);
            position.setStateTime(LocalDateTime.now());
        }

        eligibleCart.setAmount(totalOrderAmount);

        DeliveryRequisite req = new DeliveryRequisite();
        req.setDeliveryCountry("Россия");
        req.setDeliveryCity(params.getCity());
        req.setDeliveryAddress(params.getAddress());
        req.setDeliveryName(params.getName());
        req.setDeliveryPhone(params.getPhone());
        req.setDeliveryZipCode(params.getZipCode());
        req.setDeliveryExtensiveAddress(params.getExtensiveAddress());

        eligibleCart.setDeliveryRequisite(req);
        eligibleCart.setComment(params.getComment());

        /* по умолчанию сохранять введенные данные доставки в профиле */
        finalLoggedInUser.setDeliveryRequisite(req);

        if (nullablePassword != null) {
            Context context = new Context();
            context.setVariable("login", params.getEmail());
            context.setVariable("password", nullablePassword);

            String text = templateEngine.process(
                    "mail/silent-registration", context);

            sender.sendViaRegistrationBox(params.getEmail(),
                    "Пароль от учетной записи OSKELLY", text);
        }

        Order backedUpCart = createBackedUpCartFrom(eligibleCart);
        orderRepository.save(backedUpCart);

        return eligibleCart;
    }

    @Override
    public long getCartSize(User user) {

        Order cart = orderRepository.findOne(
                order.state.eq(OrderState.CREATED)
                        .and(order.amount.isNull())
                        .and(order.buyer.eq(user)));

        if (cart == null) {
            return 0;
        }

        return cart.getOrderPositions().stream()
                .filter(position -> productIsAvailable(position.getProductItem(), user))
                .count();
    }

    @Override
    public long getCartSize(String guest) {
        Order cart = orderRepository.findOne(
                order.state.eq(OrderState.CREATED)
                        .and(order.amount.isNull())
                        .and(order.guestToken.eq(guest)));

        if (cart == null) {
            return 0;
        }

        return cart.getOrderPositions().stream()
                .filter(position -> productIsAvailable(position.getProductItem(), null))
                .count();
    }

    @Override
    @EventListener
    public void populate(ProductModelEvent e) {

        Product             product      = e.getProduct();
        User                nullableUser = e.getNullableUser();
        String              guestToken   = e.getNullableGuestToken();
        Map<String, Object> view         = e.getView();

        ProductCartability cartability = nullableUser != null?
                checkProductCartability(product, nullableUser)
                : checkProductCartability(product, guestToken);

        view.put("cartability", cartability);
    }

    @Override
    @EventListener
    @Transactional
    public void merge(CustomAuthenticationSuccessEvent event) {

        User authenticatedUser = event.getAuthenticatedUser();
        String guestToken      = event.getGuestToken();

        try {
            reallyMerge(authenticatedUser, guestToken);
        }
        catch (Exception e) {
            log.error("Ошибка объединения корзин. Пользователь с id: {}, анонимная корзина: {}",
                    authenticatedUser.getId(), guestToken, e);
        }
    }

    private void reallyMerge(User user, String guestToken) {
        if (guestToken == null) { return; }

        Order guestCart = orderRepository.findOne(
                order.guestToken.eq(guestToken)
                        .and(order.state.eq(OrderState.CREATED))
                        .and(order.amount.isNull()));

        if (guestCart == null) { return; }

        Order userCart = orderRepository.findOne(
                order.buyer.eq(user)
                        .and(order.state.eq(OrderState.CREATED))
                        .and(order.amount.isNull()));

        if (userCart == null) {
            guestCart.setGuestToken(null);
            guestCart.setBuyer(user);

            return;
        }

        List<OrderPosition> guestCartItems = guestCart.getOrderPositions();

        Iterator<OrderPosition> guestCartIterator = guestCartItems.iterator();
        while (guestCartIterator.hasNext()) {

            OrderPosition guestCartItem  = guestCartIterator.next();

            BooleanExpression itemWithSameProduct =
                    orderPosition.order.eq(userCart)
                            .and(orderPosition.productItem.product.eq(
                                    guestCartItem.getProductItem()
                                            .getProduct()));

            OrderPosition cartItemWithSameProduct
                    = positionRepository.findOne(itemWithSameProduct);

            /* переместить товар из анонимной корзины,
            если такового нет в корзине пользователя. */
            if (cartItemWithSameProduct == null) {
                guestCartItem.setOrder(userCart);
            }
            else {
                /* если такой товар у пользователя есть, но пользователь
                добавил его раньше того, что есть в анонимной корзине,
                то заменить товар из пользовательской корзины товаром из анонимной корзины. */
                LocalDateTime cartItemWithSameProductAddedAt = cartItemWithSameProduct.getStateTime();
                LocalDateTime guestCartItemAddedAt = guestCartItem.getStateTime();

                if (cartItemWithSameProductAddedAt.isBefore(guestCartItemAddedAt)) {
                    guestCartItem.setOrder(userCart);
                    userCart.removePosition(cartItemWithSameProduct);
                }
                else {
                    guestCartIterator.remove();
                    guestCart.removePosition(guestCartItem);
                }
            }
        }
    }

    @Override
    public void removeObsoleteCarts() {}

    @Override
    @Transactional
    public void changeSize(Long id, Long newSizeId, User user) {

        BooleanExpression itemToUpdate = orderPosition.id.eq(id)
                .and(orderPosition.order.buyer.eq(user))
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        OrderPosition positionToUpdate  = positionRepository.findOne(itemToUpdate);
        if (positionToUpdate == null) return;

        ProductItem itemWithGivenSize = itemRepository.findOne(
                productItem.state.eq(ProductItem.State.INITIAL)
                .and(productItem.size.id.eq(newSizeId))
                .and(productItem.deleteTime.isNull())
                .and(productItem.product.eq(positionToUpdate.getProductItem().getProduct()))
                .and(productItem.product.productState.eq(ProductState.PUBLISHED))
        );
        if (itemWithGivenSize == null) return;

        positionToUpdate.setProductItem(itemWithGivenSize);
    }

    @Override
    @Transactional
    public void changeSize(Long id, Long newSizeId, String guest) {

        BooleanExpression itemToUpdate = orderPosition.id.eq(id)
                .and(orderPosition.order.guestToken.eq(guest))
                .and(orderPosition.order.state.eq(OrderState.CREATED))
                .and(orderPosition.order.amount.isNull());

        OrderPosition positionToUpdate  = positionRepository.findOne(itemToUpdate);
        if (positionToUpdate == null) return;

        ProductItem itemWithGivenSize = itemRepository.findOne(
                productItem.state.eq(ProductItem.State.INITIAL)
                        .and(productItem.size.id.eq(newSizeId))
                        .and(productItem.deleteTime.isNull())
                        .and(productItem.product.eq(positionToUpdate.getProductItem().getProduct()))
                        .and(productItem.product.productState.eq(ProductState.PUBLISHED))
        );
        if (itemWithGivenSize == null) return;

        positionToUpdate.setProductItem(itemWithGivenSize);
    }

    @Override
    @Transactional
    public void updateDelivery(User user, String guest,
                               String city, String address, String zipCode,
                               String extensiveAddress) {

        Order checkedOutOrder = getCheckedOutOrder(user, guest)
                .orElse(null);
        if (checkedOutOrder == null) return;

        DeliveryRequisite delivery = checkedOutOrder.getDeliveryRequisite();
        if (delivery == null) {
            delivery = new DeliveryRequisite();
            checkedOutOrder.setDeliveryRequisite(delivery);
        }

        delivery.setDeliveryCountry("Россия");
        delivery.setDeliveryCity(city);
        delivery.setDeliveryAddress(address);
        delivery.setDeliveryZipCode(zipCode);
        delivery.setDeliveryExtensiveAddress(extensiveAddress);
    }

    private ProductItem getCartableItemOrDie(Long productId, Long sizeId,
                                             User user, String guest)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException {

        Optional<ProductItem> availableItemIfAny
                = productItemService.findFirstAvailable(productId, sizeId);

        ProductItem availableItem
                = availableItemIfAny.orElseThrow(ProductNotFoundException::new);

        ProductCartability c = user != null?
                checkProductCartability(availableItem.getProduct(), user)
                : checkProductCartability(availableItem.getProduct(), guest);

        if (!c.productIsCartable) {
            throw new ProductCanNotBeAddedToCartException(c.nonCartabilityReason);
        }

        return availableItem;
    }

    /* Получить текущий оформляемый заказ. */
    private Optional<Order> getCheckedOutOrder(User u, String guest) {

        BooleanExpression predicate = order.state.eq(OrderState.CREATED)
                .and(order.amount.isNull())
                .and(u != null? order.buyer.eq(u) : order.guestToken.eq(guest));


        Order checkedOutOrder = orderRepository.findOne(predicate);
        return Optional.ofNullable(checkedOutOrder);
    }

    private Order newCheckoutOrder(User user, String guest) {

        Order order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setState(OrderState.CREATED);
        if (user != null) {
            order.setBuyer(user);
        }
        else if (guest != null) {
            order.setGuestToken(guest);
        }

        return order;
    }

    private void save(Order checkoutOrder, ProductItem itemToAdd) {

        OrderPosition newPosition = new OrderPosition();
        newPosition.setProductItem(itemToAdd);
        newPosition.setState(OrderPositionState.UNDEFINED);
        newPosition.setStateTime(LocalDateTime.now());
        checkoutOrder.addPosition(newPosition);

        /* обновить время изменения корзины
        (анонимные корзины удаляются по истечении определенного срока
        с момента их последнего изменения)
        */
        checkoutOrder.setState(OrderState.CREATED);

        orderRepository.save(checkoutOrder);
    }

    /** В корзину можно добавить только один экземпляр товара.
    * Другой экземпляр (с другим размером например) добавить нельзя. */
    private ProductCartability check(Product p, BooleanExpression predicate) {

        ProductCartability cartability;

        boolean productIsUnavailable = p.getProductState() != ProductState.PUBLISHED
                || !productService.productIsAvailable(p);

        if (productIsUnavailable) {
            cartability = new ProductCartability(false, "Извините, товар продан");
        }
        else if (positionRepository.exists(predicate)) { // товар уже в корзине
            cartability = new ProductCartability(false, "В корзине");
        }
        else {
            cartability = new ProductCartability(true);
        }

        return cartability;
    }

    private Cart getCart(BooleanExpression cartItemsPredicate, User nullableUser) {

        Iterable<OrderPosition> tmp = positionRepository.findAll(
                cartItemsPredicate, orderPosition.stateTime.asc());

        if (!tmp.iterator().hasNext()) { return null; }

        List<OrderPosition> rawCartItems = new ArrayList<>();
        tmp.forEach(rawCartItems::add);

        Order cartOrder = rawCartItems.get(0).getOrder();

        List<Item> cartItems = rawCartItems.stream()
                .map(orderPosition -> from(orderPosition, nullableUser))
                .collect(toList());

        Cart cart = new Cart()
                .setItems(cartItems)
                .setComment(cartOrder.getComment());

        /* Стоимость всех доступных вещей без учета скидок. */
        List<OrderPosition> availablePositions = rawCartItems.stream()
                .filter(position -> position.getProductItem().isAvailable())
                .collect(toList());

        cart.setSize(availablePositions.size())
            .setFaulty(availablePositions.isEmpty());



        Optional<Discount> discountIfAny = cartOrder.getDiscount();
        discountIfAny.ifPresent(discount ->
                cart.setDiscount(new Cart.Discount()
                        .setCode(discount.code)
                        .setValid(discount.isValidYet)
                ));

        BigDecimal deliveryPrice = availablePositions.stream()
                .map(position -> position.getProductItem().getNullableDeliveryCost())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!availablePositions.isEmpty()){

            BigDecimal totalPrice = availablePositions.stream()
                    .map(position -> getNegotiationAwarePrice(position.getProductItem(), nullableUser))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (discountIfAny.isPresent() && discountIfAny.get().isValidYet) {
                BigDecimal priceWithDiscount = cartOrder.getPriceWithDiscount(totalPrice);
                cart.setFinalPrice(Utils.prettyRoundToCents(priceWithDiscount.add(deliveryPrice)));
            }
            else {
                cart.setFinalPrice(Utils.prettyRoundToCents(totalPrice.add(deliveryPrice)));
            }
        }

        if (cartOrder.getDeliveryRequisite() != null) {
            cart.setAddress(cartOrder.getDeliveryRequisite());
        }
        else if (nullableUser != null) {
            cart.setAddress(nullableUser.getDeliveryRequisite());
        }

        return cart;
    }

    /** Заменить <b>по возможности</b> недоступные вещи, которые уже находятся в корзине, на доступные
     * (которые могут быть отличатся по цене от заменяемой вещи).
     * Удаляет скидку если она невалидна.
     **/
    private void fixCartIfTheCase(User u, String guest) {

        QProductItem cartItem = orderPosition.productItem;

        BooleanExpression itemIsUnavailable = cartItem.deleteTime.isNotNull()
                .or(cartItem.state.ne(ProductItem.State.INITIAL))
                .or(cartItem.product.productState.ne(ProductState.PUBLISHED));

        BooleanExpression cartOwner = u != null? orderPosition.order.buyer.eq(u)
                : orderPosition.order.guestToken.eq(guest);

        Iterable<OrderPosition> unavailableCartItems = positionRepository.findAll(
                cartOwner.and(itemIsUnavailable));

        for (OrderPosition unavailableCartItem : unavailableCartItems) {

            Long itemProductId = unavailableCartItem.getProductItem().getProduct().getId();
            Long itemSizeId = unavailableCartItem.getProductItem().getSize().getId();

            Optional<ProductItem> replacingProductItem
                    = productItemService.findFirstAvailable(itemProductId, itemSizeId);

            if (replacingProductItem.isPresent()) {

                OrderPosition replacingCartItem = new OrderPosition();
                replacingCartItem.setOrder(unavailableCartItem.getOrder());
                replacingCartItem.setStateTime(unavailableCartItem.getStateTime());
                replacingCartItem.setState(OrderPositionState.UNDEFINED);
                replacingCartItem.setProductItem(replacingProductItem.get());

                positionRepository.save(replacingCartItem);
                positionRepository.delete(unavailableCartItem);
            }
        }

        BooleanExpression cartPredicate = order.state.eq(OrderState.CREATED)
                .and(order.amount.isNull());

        Order cart = orderRepository.findOne(cartPredicate
                .and(u != null? order.buyer.eq(u) : order.guestToken.eq(guest)));
        if (cart == null) return;

        if (cart.getGiftCard() != null) {
            GiftCard gc = cart.getGiftCard();
            boolean another = orderRepository.exists(
                    QOrder.order.giftCard.id.eq(gc.getId())
                            .and(QOrder.order.ne(cart))
                            .and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
                                    OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED)));
            if (another || gc.isExpired()) {
                cart.setGiftCard(null);
            }
        }

        if (cart.getPromoCode() != null && u != null) {
            PromoCode pc = cart.getPromoCode();
            boolean another = orderRepository.exists(
                    QOrder.order.promoCode.eq(pc)
                            .and(QOrder.order.ne(cart))
                            .and(QOrder.order.state.in(OrderState.HOLD, OrderState.HOLD_COMPLETED,
                                    OrderState.COMPLETION_PROCESSING, OrderState.COMPLETED)));
            if (another) {
                cart.setPromoCode(null);
            }
        }

        if (cart.getPromoCode() != null) {
            PromoCode promoCode = cart.getPromoCode();
            BigDecimal currentPrice = cart.getOrderPositions().stream()
                    .map(position -> getNegotiationAwarePrice(position.getProductItem(), u))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (promoCode.isExpired() || !promoCode.supports(currentPrice)) {
                cart.setPromoCode(null);
            }
        }
    }

    private Item from(OrderPosition rawCartItem, User u) {

        Item item = new Item();

        ProductItem productItem = rawCartItem.getProductItem();
        Product     product     = productItem.getProduct();

        item.setId(rawCartItem.getId())
            .setProductId(product.getId())
            .setProductName(product.getDisplayName())
            .setBrandName(product.getBrand().getName())
            .setProductCondition(product.getProductCondition().getName())
            .setSize(from(productItem.getSize(), product.getSizeType()))
            .setAvailable(productIsAvailable(productItem, u));

        Optional<ProductImage> primaryImage = imageService.getPrimaryImage(product);
        item.setImageUrl(primaryImage.map(ProductImage::getUrl).orElse(null));

        /* Учитывать цену на товар, которую покупатель согласовал с продавцом в процессе торгов. */
        BigDecimal itemPrice = getNegotiationAwarePrice(productItem, u);
        Item.Price price = new Item.Price()
                .setFormatted(Utils.prettyRoundToCents(itemPrice))
                .setRaw(itemPrice);
        item.setProductPrice(price);

        BigDecimal recommendedPrice = product.getRrpPrice();
        if (recommendedPrice != null) {

            BigDecimal savings = recommendedPrice.subtract(itemPrice)
                    .divide(recommendedPrice, 2, BigDecimal.ROUND_DOWN)
                    .movePointRight(2);

            Item.Rrp rrp = new Item.Rrp()
                     .setValue(Utils.formatPrice(recommendedPrice))
                     .setSavings(savings.toPlainString());

            item.setRrp(rrp);
        }


        List<Size> availableSizes = productService.getAvailableSizes(product);
        item.setAvailableSizes(availableSizes.stream()
                /* преобразовать в dto */
                .map(s -> from(s, product.getSizeType()))
                .collect(toList()));

        return item;
    }

    /** Для отображения доступных размеров в виде RUS 36 */
    private Item.Size from(Size s, SizeType t) {
        return t == SizeType.NO_SIZE ? null :
                new Item.Size()
                        .setId(s.getId())
                        .setValue(String.format("%s %s", t.getAbbreviation(), s.getBySizeType(t)));
    }

    private boolean productIsAvailable(ProductItem item, User nullableUser) {
        if (nullableUser != null) {
            Product product = item.getProduct();
            Optional<BigDecimal> negotiatedPrice
                    = productService.getNegotiatedPrice(product, nullableUser);

            if (negotiatedPrice.isPresent()) {
                return item.isAvailable();
            }
            else {
                return item.isAvailable()
                || productItemService.getItemLikeThisThatCanBeOrdered(item).isPresent();
            }
        }

        return item.isAvailable()
                || productItemService.getItemLikeThisThatCanBeOrdered(item).isPresent();
    }

    /**
     * Заказчик хочет, чтобы пользователь после оплаты заказа возвращался обратно в ту же
     * самую корзину, которая у него была до момента перехода на эту самую оплату.
     *
     * Для этого мы перед оплатой заказа сначала создаем копию текущей корзины, а потом
     * преобразовываем первую, оригинальную корзину, в заказ, который пользователь и оплачивает.
     *
     * @param cartToPay корзина, которую пользователь собирается оплатить.
     *
     * @apiNote вызывать только непосредственно перед оплатой корзины.
     * */
    private Order createBackedUpCartFrom(Order cartToPay) {
        Order backedUpCart = new Order();

        backedUpCart.setUuid(UUID.randomUUID());
        backedUpCart.setState(OrderState.CREATED);
        backedUpCart.setBuyer(cartToPay.getBuyer());
        backedUpCart.setGuestToken(cartToPay.getGuestToken());
        backedUpCart.setDeliveryRequisite(cartToPay.getDeliveryRequisite());
        backedUpCart.setComment(cartToPay.getComment());

        backedUpCart.setPromoCode(cartToPay.getPromoCode());
        backedUpCart.setGiftCard(cartToPay.getGiftCard());

        /* перенести позиции из оплачиваемого заказа в новую корзину. */
        cartToPay.getOrderPositions().forEach(
                p -> backedUpCart.addPosition(clone(p)));

        return backedUpCart;
    }

    private OrderPosition clone(OrderPosition cartPosition) {
        OrderPosition cloned = new OrderPosition();
        cloned.setProductItem(cartPosition.getProductItem());
        cloned.setState(OrderPositionState.UNDEFINED);
        cloned.setStateTime(LocalDateTime.now());

        return cloned;
    }

    /** @return согласованная цена, если есть, или текущая цена вещи. */
    private BigDecimal getNegotiationAwarePrice(ProductItem item, User nullableUser) {

        BigDecimal negotiationAwarePrice = item.getCurrentPrice();
        /* аноним не имеет возможности торговаться с продавцом */
        if (nullableUser != null) {
            Optional<BigDecimal> negotiatedPrice = productService.getNegotiatedPrice(
                    item.getProduct(), nullableUser);

            boolean negotiatedPriceIsValid = negotiatedPrice.isPresent()
                    && item.getCurrentPrice().compareTo(negotiatedPrice.get()) > -1;
            if (negotiatedPriceIsValid) {
                negotiationAwarePrice = negotiatedPrice.get();
            }
        }

        return negotiationAwarePrice;
    }
}
