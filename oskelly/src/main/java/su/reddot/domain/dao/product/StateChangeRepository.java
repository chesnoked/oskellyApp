package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.product.StateChange;

public interface StateChangeRepository
        extends JpaRepository<StateChange, Long>, QueryDslPredicateExecutor<StateChange> {
}
