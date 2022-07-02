package su.reddot.domain.dao.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 22.04.17.
 */
public interface OrderRepository extends JpaRepository<Order, Long>, QueryDslPredicateExecutor<Order> {

    /**
     * Найти заказ, который сделан указанным пользователем
     * @param orderId идентификатор заказа
     * @param buyer пользователь, который сделал заказ
     * @return общие данные заказа
     */
    Optional<Order> findByIdAndBuyer(Long orderId, User buyer);

    /**
     * Найти заказы по списку состояний
     * @param states
     * @return
     */
    List<Order> findAllByStateInOrderByStateTimeDesc(List<OrderState> states);

    /**
     * @param promoCode интересующий промо-код
     * @param buyer пользователь, который оформил заказ
     * @return заказ, который оформил указанный пользователь, и в котором уже используется данный промо-код,
     */
    Optional<Order> findByPromoCodeAndBuyer(PromoCode promoCode, User buyer);

    /**
     * @param id подарочный сертификат
     * @return заказ, в котором уже используется этот сертификат (сертификат может использоваться только в одном заказе в рамках всех пользователей)
     */
    Optional<Order> findByGiftCardId(Long id);
}
