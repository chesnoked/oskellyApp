package su.reddot.domain.service.notificationTransport;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.notification.Notification;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
@RequiredArgsConstructor
@Getter
public class NotificationTransportEvent {

	private final Notification notification;
}
