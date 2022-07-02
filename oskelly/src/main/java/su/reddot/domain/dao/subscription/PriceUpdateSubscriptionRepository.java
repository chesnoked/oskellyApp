package su.reddot.domain.dao.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.subscription.PriceUpdateSubscription;
import su.reddot.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface PriceUpdateSubscriptionRepository extends JpaRepository<PriceUpdateSubscription, Long>{

    List<PriceUpdateSubscription> getAllBySubscriber(User subscriber);

    List<PriceUpdateSubscription> getAllByProduct(Product product);

    Long countAllByProduct(Product product);

    Optional<PriceUpdateSubscription> findBySubscriberAndProduct(User subscriber, Product product);

    /** Перестать отслеживать товар. */
    void deleteByProductIdAndSubscriber(Long productId, User u);
}
