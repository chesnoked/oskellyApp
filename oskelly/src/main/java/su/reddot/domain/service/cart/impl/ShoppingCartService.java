package su.reddot.domain.service.cart.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.ShoppingCartRepository;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.ShoppingCart;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.product.QProductItem;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.*;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.cart.impl.dto.GuestOrderCreationParams;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.infrastructure.configuration.CustomAuthenticationSuccessEvent;
import su.reddot.presentation.Utils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static su.reddot.domain.model.QShoppingCart.shoppingCart;

@Service
@RequiredArgsConstructor
public class ShoppingCartService implements CartService<CartView> {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ImageService           imageService;

    /* FIXME желательно перенести всю логику работы с товаром в ProductService;
    * Сущность "вещь" - это детали реализации товара. */
    private final ProductItemService     productItemService;

    private final ProductService productService;
    private final OrderService   orderService;

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
    public Long addItem(Long productId, Long sizeId, User u)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException {

        Optional<ProductItem> availableItemIfAny = productItemService.findFirstAvailable(productId, sizeId);
        ProductItem availableItem = availableItemIfAny.orElseThrow(ProductNotFoundException::new);

        ProductCartability c = checkProductCartability(availableItem.getProduct(), u);
        if (!c.productIsCartable) {
            throw new ProductCanNotBeAddedToCartException(c.nonCartabilityReason);
        }

        ShoppingCart shoppingCart = shoppingCartRepository.save(new ShoppingCart(u, availableItem));
        return shoppingCart.getId();
    }

    @Override
    public Long addItem(Long productId, Long sizeId, String guest) {
        throw new UnsupportedOperationException();
    }

    /**
     * Убрать позицию товара из корзины.
     *
     * @param itemId идентификатор <b>позиции</b> товара в корзине
     * @param u пользователь, из корзины которого нужно убрать товар
     */
    @Transactional
    public void removeItem(Long itemId, User u) {
        shoppingCartRepository.deleteProductInCartByIdAndUser(
                itemId, u);
    }

    @Override
    public void removeItem(Long itemId, String guest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProductCartability checkProductCartability(Product product, User u) {

        /* в корзину можно добавить только один экземпляр товара.
        * Другой экземпляр (с другим размером например) добавить нельзя. */
        boolean productIsAlreadyInCart
                = shoppingCartRepository.existsByProductItemProductAndUser(product, u);

        if (productIsAlreadyInCart) {
            return new ProductCartability(false, "В корзине");
        } else {

            if (product.isAvailable() && !product.isSold()) {
                return new ProductCartability(true, null);
            }
            else {
                // во всех остальных (неизвестных) случаях товар нельзя добавить в корзину
                return new ProductCartability(false, "Нет в продаже");
            }
        }
    }

    @Override
    public ProductCartability checkProductCartability(Product product, String guest) {
        throw new UnsupportedOperationException();
    }

    /**
     * Получить содержимое корзины пользователя.
     * ВАЖНО!!! Используется в ShoppingCartRestControllerV1, править осторожно, можно сломать мобильное приложение
     * @param u пользователь - владелец корзины
     * @return содержимое корзины
     */
    public CartView getCart(User u) {

        fixCartIfTheCase(u);

        List<ShoppingCart> rawCartItems = shoppingCartRepository.findByUserOrderByAddTime(u);
        CartView cartView = new CartView();

        List<CartItem> cartItems = rawCartItems.stream().map(i -> of(i, u)).collect(Collectors.toList());
        BigDecimal totalPrice = cartItems.stream()
                .map(i -> i.productPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)

                /* отображать стоимость корзины с точностю до копеек */
                .setScale(2, RoundingMode.UP);

        BigDecimal effectivePrice = cartItems.stream()
                .filter(i -> i.isEffective)
                .map(i -> i.productPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryCost = cartItems.stream()
                .filter(i -> i.isEffective)
                .map(i -> i.deliveryCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal effectivePriceWithDeliveryCost = effectivePrice.add(deliveryCost);

        cartView.setItems(cartItems)
                .setTotalPrice(totalPrice.toString())
                .setEffectivePrice(Utils.prettyRoundToCents(effectivePrice))
                .setDeliveryCost(Utils.prettyRoundToCents(deliveryCost))
                .setEffectivePriceWithDeliveryCost(Utils.prettyRoundToCents(effectivePriceWithDeliveryCost));

        return cartView;
    }

    @Override
    public CartView getCart(String guest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDiscount(String code, User u)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDiscount(String code, String guest)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDiscount(User u) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDiscount(String guest) {
        throw new UnsupportedOperationException();
    }

    /** Заменить <b>по возможности</b> недоступные вещи, которые уже находятся в корзине, на доступные
     * (которые могут быть отличатся по цене от заменяемой вещи).
     **/
    private void fixCartIfTheCase(User u) {

        QProductItem cartItem = shoppingCart.productItem;

        BooleanExpression itemIsUnavailable = cartItem.deleteTime.isNotNull()
                .or(cartItem.state.ne(ProductItem.State.INITIAL))
                .or(cartItem.product.productState.ne(ProductState.PUBLISHED));

        Iterable<ShoppingCart> unavailableCartItems = shoppingCartRepository.findAll(shoppingCart.user.eq(u).and(itemIsUnavailable));

        for (ShoppingCart unavailableCartItem : unavailableCartItems) {

            Long itemProductId = unavailableCartItem.getProductItem().getProduct().getId();
            Long itemSizeId = unavailableCartItem.getProductItem().getSize().getId();

            Optional<ProductItem> replacingProductItem = productItemService.findFirstAvailable(itemProductId, itemSizeId);
            if (replacingProductItem.isPresent()) {

                ShoppingCart replacingCartItem = new ShoppingCart(u, replacingProductItem.get());
                shoppingCartRepository.save(replacingCartItem);
                shoppingCartRepository.delete(unavailableCartItem);
            }
        }
    }

    private CartItem of(ShoppingCart rawCartItem, User u) {

        ProductItem productItem = rawCartItem.getProductItem();
        Product     product     = productItem.getProduct();

        Optional<ProductImage> primaryImage = imageService.getPrimaryImage(product);

        /* Учитывать цену на товар, которую покупатель согласовал с продавцом в процессе торгов. */
        BigDecimal productPrice = productService.getNegotiatedPrice(product, u)
                .orElse(productItem.getCurrentPrice().setScale(2, RoundingMode.UP));

        return CartItem.builder()
                .itemId(rawCartItem.getId())
                .productId(product.getId())
                .productName(product.getDisplayName())

                /* данные, которые затрагивают кошелек пользователя. */
                .productPrice(productPrice)
                .deliveryCost(productItem.getNullableDeliveryCost())

                .productSize(productItem.getConcreteSizePretty().orElse(null))

                .brandName(product.getBrand().getName())

                .imageUrl(primaryImage.map(ProductImage::getUrl).orElse(null))

                .isEffective(productIsEffective(productItem))

                .build();
    }

    /**
     * Попытаться создать заказ на основе переданных вещей.
     * Если вещь не найдена у пользователя в корзине, она не попадает в заказ.
     * Если вещь недоступна для продажи, попытаться найти похожую вещь и добавить ее в заказ.
     * @return данные о созданном заказе
     */
    @Transactional
    public OrderCreationResult createOrder(List<Long> itemIds, User loggedInUser) {

        List<CartItem> productItemsThatCanNotBeOrdered = new ArrayList<>();
        List<ProductItem> productItemsThatCanBeOrdered = new ArrayList<>();
        for (Long itemId : itemIds) {

            Optional<ShoppingCart> cartItem = shoppingCartRepository.findByIdAndUser(itemId, loggedInUser);
            if (!cartItem.isPresent()) { continue; }

            ShoppingCart existingCartItem = cartItem.get();

            ProductItem productItem = existingCartItem.getProductItem();
            if (productItem.isAvailable()) {
                productItemsThatCanBeOrdered.add(productItem);
                continue;
            }

            Optional<ProductItem> itemLikeThisThatCanBeOrdered = productItemService.getItemLikeThisThatCanBeOrdered(productItem);
            if (itemLikeThisThatCanBeOrdered.isPresent()) {
                productItemsThatCanBeOrdered.add(itemLikeThisThatCanBeOrdered.get());
            }
            else {
                Product product = productItem.getProduct();
                Optional<ProductImage> primaryImage = imageService.getPrimaryImage(product);

                CartItem item = CartItem.builder()
                    .itemId(productItem.getId())

                    .productId(product.getId())
                    .productName(product.getDisplayName())
                    .productPrice(productItem.getCurrentPrice().setScale(2, RoundingMode.UP))
                    .productSize(productItem.getConcreteSizePretty().orElse(null))

                    .brandName(product.getBrand().getName())

                    .imageUrl(primaryImage.map(ProductImage::getUrl).orElse(null))

                    .build();
                productItemsThatCanNotBeOrdered.add(item);
            }
        }

        if (productItemsThatCanBeOrdered.size() == 0) {
            throw new OrderCreationException("Ни одного товара нет в продаже");
        }

        Order order = orderService.createOrder(loggedInUser, new HashSet<>(productItemsThatCanBeOrdered));

        shoppingCartRepository.deleteByUser(loggedInUser);

        return new OrderCreationResult(productItemsThatCanNotBeOrdered, order);
    }

    @Override
    public Order createGuestOrder(GuestOrderCreationParams params) {
        throw new UnsupportedOperationException();
    }

    public long getCartSize(User user) {

        return shoppingCartRepository.count(shoppingCart.user.eq(user));
    }

    @Override
    public long getCartSize(String guest) {
        throw new UnsupportedOperationException();
    }

    /**
     * @apiNote метод не выполняет никакой работы,
     * see {@link DefaultCartService#populate(ProductModelEvent)}
     */
    @Override
    public void populate(ProductModelEvent e) {}

    @Override
    public void merge(CustomAuthenticationSuccessEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeObsoleteCarts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changeSize(Long id, Long newSizeId, User user) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void changeSize(Long id, Long newSizeId, String guest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDelivery(User user, String guest, String city, String address, String zipCode, String extensiveAddress) {
        throw new UnsupportedOperationException();
    }

    /* Получить причину, по которой товар недоступен для покупки*/
    private Optional<String> getNonEffectivenessReason(ProductItem nonEffectiveProductItem) {
        if (!nonEffectiveProductItem.getProduct().isAvailable()) {
            return Optional.of("Товар снят с продажи");
        }

        /*
          TODO: нужно проверять все позиции товара определенного размера
         */
        if (nonEffectiveProductItem.isSold()) {
            return Optional.of("Товар продан");
        }

        if (nonEffectiveProductItem.isReserved()) {
            return Optional.of(
                    "Товар забронирован на " + nonEffectiveProductItem.getReservationExpiryInMinutes()
                    + " минут");
        }

        if (nonEffectiveProductItem.isInPendingOrder()) {
            return Optional.of("Товар временно находится в другом заказе");
        }

        return Optional.empty();
    }

    private boolean productIsEffective(ProductItem item) {
        return item.isAvailable()
                || productItemService.getItemLikeThisThatCanBeOrdered(item).isPresent();
    }
}

