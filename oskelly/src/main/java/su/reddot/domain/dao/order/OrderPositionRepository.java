package su.reddot.domain.dao.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 22.04.17.
 */
public interface OrderPositionRepository extends JpaRepository<OrderPosition, Long>,
        QueryDslPredicateExecutor<OrderPosition> {

    /**
     * @param o заказ
     * @param i вещь, которую нужно найти в заказе
     * @return позиция заказа, которая содержит указанную вещь
     */
    Optional<OrderPosition> findByOrderAndProductItem(Order o, ProductItem i);

    /**
     * Получить все продажи по конкретному пользователю
     * @param seller продавец
     * @return список продаж
     */
    List<OrderPosition> findByProductItemProductSellerAndOrderStateIn(User seller, Collection<OrderState> states);
}