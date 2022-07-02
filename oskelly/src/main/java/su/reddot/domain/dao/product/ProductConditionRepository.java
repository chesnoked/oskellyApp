package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.product.ProductCondition;

import java.util.Optional;

public interface ProductConditionRepository extends JpaRepository<ProductCondition, Long> {

    Optional<ProductCondition> findByName(@SuppressWarnings("SameParameterValue") String name);

}
