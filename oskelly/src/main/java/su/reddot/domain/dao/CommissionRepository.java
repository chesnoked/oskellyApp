package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.Commission;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.user.User;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 06.08.17.
 */
public interface CommissionRepository extends JpaRepository<Commission, Long> {

	Optional<Commission> findFirstByUserAndCategoryAndType(User u, Category c, Commission.Type t);

	Optional<Commission> findFirstByCategoryAndType(Category c, Commission.Type t);

	Optional<Commission> findFirstByType(Commission.Type t);

	@Query("select c from Commission c where c.type = ?1 and ?2 between c.startPrice and c.endPrice")
	Optional<Commission> findByTypeAndPriceBetween(Commission.Type t, BigDecimal price);
}
