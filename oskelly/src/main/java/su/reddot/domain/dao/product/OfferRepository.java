package su.reddot.domain.dao.product;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.product.Offer;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long>, QueryDslPredicateExecutor<Offer> {

    List<Offer> findByProductAndOfferor(Product p, User offeror, Sort s);

    /** Предложенная цена для товара. Проверить ее может только продавец товара. */
    Optional<Offer> findByIdAndProductSeller(Long offerId, User productSeller);

    /**
     * @return согласованная в процессе торгов с продавцом цена товара, если таковая есть.
     */
    Optional<Offer> findOneByProductAndOfferorAndIsAcceptedIsTrue(Product p, User offeror);
}
