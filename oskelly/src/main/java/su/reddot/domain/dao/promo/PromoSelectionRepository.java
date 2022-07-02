package su.reddot.domain.dao.promo;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.promo.PromoSelection;

import java.util.List;

public interface PromoSelectionRepository extends JpaRepository<PromoSelection, Long> {

	List<PromoSelection> findAllByOrderByOrderIndexAsc();

	List<PromoSelection> findAllByPromoGroupInOrderByOrderIndexAsc(PromoSelection.PromoGroup group);

	int countByPromoGroup(PromoSelection.PromoGroup g);
}
