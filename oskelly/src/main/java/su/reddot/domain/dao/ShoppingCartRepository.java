package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.ShoppingCart;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long>, QueryDslPredicateExecutor<ShoppingCart> {

    /**
     * Убрать товар из корзины пользователя
     * @param id идентификатор позиции товара в корзине
     * @param u пользователь
     */
    void deleteProductInCartByIdAndUser(Long id, User u);

    /**
     * Очистить корзину
     * @param u пользователь
     */
    void deleteByUser(User u);

    /**
     * Проверить, существует ли данный товар в корзине пользователя
     * @return true, если товар существует, иначе false
     */
    boolean existsByProductItemProductAndUser(Product p, User u);

    /**
     * Получить содержимое корзины пользователя
     * @return список товаров в корзине
     */
    List<ShoppingCart> findByUserOrderByAddTime(User u);

    /**
     * Убрать товары из корзины по их идентификаторам
     * @param productItemIds идентификаторы товаров
     * @param u пользователь - владелец корзины
     */
    void deleteProductInCartByProductItemIdAndUser(List<Long> productItemIds, User u);

    Optional<ShoppingCart> findByIdAndUser(Long itemId, User loggedInUser);
}
