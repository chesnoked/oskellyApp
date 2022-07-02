package su.reddot.domain.dao.logistic;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.logistic.Waybill;
import su.reddot.domain.model.order.OrderPosition;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 28.08.17.
 */
public interface WaybillRepository extends JpaRepository<Waybill, Long> {

    List<Waybill> findAllByOrderPositionOrderByIdDesc(OrderPosition op);
}
