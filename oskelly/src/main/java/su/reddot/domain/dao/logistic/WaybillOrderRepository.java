package su.reddot.domain.dao.logistic;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.logistic.WaybillOrder;

/**
 * @author Vitaliy Khludeev on 28.08.17.
 */
public interface WaybillOrderRepository extends JpaRepository<WaybillOrder, Long> {

}
