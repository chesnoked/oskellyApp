package su.reddot.domain.dao.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.discount.PromoCode;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer> {

    Optional<PromoCode> findByCode(String code);

}
