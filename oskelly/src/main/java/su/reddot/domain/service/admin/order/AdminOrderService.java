package su.reddot.domain.service.admin.order;

import java.util.List;

public interface AdminOrderService {

    /**
     * получаем все заказы в статусе HOLD
     * @return
     */
    List<AdminOrderView> findAllActive();

    /**
     * получаем все заказы требующие внимания:
     * HOLD_PROCESSING, HOLD_ERROR, CANCELED
     * @return
     */
    List<AdminOrderView> findAllInWarningState();
}
