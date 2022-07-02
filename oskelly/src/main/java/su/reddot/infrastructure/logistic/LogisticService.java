package su.reddot.infrastructure.logistic;

import su.reddot.domain.model.logistic.event.SaleConfirmedEvent;
import su.reddot.domain.model.order.OrderPosition;

/**
 * @author Vitaliy Khludeev on 27.08.17.
 */
public interface LogisticService {

	@SuppressWarnings("unused") /* вызывается неявно при появлении соответствующего события */
	void createWaybill(SaleConfirmedEvent e);
}
