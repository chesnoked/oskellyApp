package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.product.ProductStatus;

import java.util.Optional;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, Long> {

	Optional<ProductStatus> findByName(String name);
}
