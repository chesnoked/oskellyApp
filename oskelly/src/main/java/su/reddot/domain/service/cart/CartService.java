package su.reddot.domain.service.cart;

import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.cart.impl.dto.GuestOrderCreationParams;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.infrastructure.configuration.CustomAuthenticationSuccessEvent;

import java.util.List;

public interface CartService<T> {

    Long addItem(Long productId, Long sizeId, User u)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException;

    Long addItem(Long productId, Long sizeId, String guest)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException;

    void removeItem(Long itemId, User u);
    void removeItem(Long itemId, String guest);

    ProductCartability checkProductCartability(Product product, User u);
    ProductCartability checkProductCartability(Product product, String guest);

    /** Получить содержимое корзины.
     * @apiNote возвращает null если корзины физически не существует
     * либо если она пуста.*/
    T getCart(User u);
    T getCart(String guest);

    /** Назначить скидку для корзины. */
    void addDiscount(String code, User u)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException;
    void addDiscount(String code, String guest)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException;

    void removeDiscount(User u);
    void removeDiscount(String guest);

    OrderCreationResult createOrder(List<Long> itemIds, User loggedInUser);
    /** Создать заказ из тех товаров корзины, которые доступны на данный момент. */
    Order createGuestOrder(GuestOrderCreationParams params);

    /** Число доступных для покупки товаров в корзине зарегистрированного пользователя. */
    long getCartSize(User user);
    /** Число доступных для покупки товаров в корзине гостя. */
    long getCartSize(String guest);

    void populate(ProductModelEvent e);

    void merge(CustomAuthenticationSuccessEvent event);

    void removeObsoleteCarts();

    void changeSize(Long id, Long newSizeId, User user);
    void changeSize(Long id, Long newSizeId, String guest);

    void updateDelivery(User user, String guest,
                        String city, String address, String zipCode,
                        String extensiveAddress);
}
