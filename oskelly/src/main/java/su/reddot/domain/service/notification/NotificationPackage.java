package su.reddot.domain.service.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.user.User;

/**
 * @author Vitaliy Khludeev on 20.12.17.
 */
@RequiredArgsConstructor
@Getter
public class NotificationPackage<T extends Notification> {
	private final User user;
	private final T templateNotification;
}
