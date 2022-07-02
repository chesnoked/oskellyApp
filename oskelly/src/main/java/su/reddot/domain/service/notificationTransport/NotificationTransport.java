package su.reddot.domain.service.notificationTransport;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
public interface NotificationTransport {

	@TransactionalEventListener(
			phase = TransactionPhase.AFTER_COMMIT,
			fallbackExecution = true)
	@Async
	void send(NotificationTransportEvent e);
}
