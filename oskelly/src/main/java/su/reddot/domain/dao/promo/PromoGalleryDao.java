package su.reddot.domain.dao.promo;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.promo.PromoGallery;

import java.util.List;

public interface PromoGalleryDao extends JpaRepository<PromoGallery, Long> {

	List<PromoGallery> findAllByOrderByOrderIndexAsc();
}
