package su.reddot.domain.dao.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.user.User;

import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    Optional<GiftCard> findByCode(String code);

    Optional<GiftCard> findByIdAndBuyer(Long giftCardId, User buyer);

    boolean existsByCode(String code);
}
