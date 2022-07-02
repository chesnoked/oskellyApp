package su.reddot.domain.service.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.user.User;

import java.util.List;
import java.util.TimeZone;

/**
 * @author Vitaliy Khludeev on 18.09.17.
 */
public interface NotificationService {

	Long countUnreadNotificationsByUser(Long userId);

	List<NotificationView> findNewsByUserAndPresentAsView(Long userId, boolean isPrivate, TimeZone nullableTimeZone);

	List<NotificationView> findNotificationsByUserAndPresentAsView(Long userId, TimeZone nullableTimeZone);

	NotificationView of(Notification n, boolean needImageOfTargetObject, TimeZone nullableTimeZone);

	boolean read(Long notificationId);

	/** Пометить все уведомления как прочитанные. */
	void readAll(User u);

	@TransactionalEventListener(
			phase = TransactionPhase.AFTER_COMMIT,
			fallbackExecution = true)
	void create(Notification notification);

	/**
	 * Опубликовать нотификацию для всех подписчиков пользователя
	 * @param notificationPackage пакет однотиптых нотификаций, которые нужно опубликовать для подписчиков определенного пользователя
	 */
	@TransactionalEventListener(
			phase = TransactionPhase.AFTER_COMMIT,
			fallbackExecution = true)
	void create(NotificationPackage<? extends Notification> notificationPackage);
}
