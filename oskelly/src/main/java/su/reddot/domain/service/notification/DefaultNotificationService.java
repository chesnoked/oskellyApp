package su.reddot.domain.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.notification.NotificationRepository;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.notificationTransport.NotificationTransportEvent;
import su.reddot.domain.service.user.UserService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 18.09.17.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultNotificationService implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserService userService;
	private final JpaContext jpaContext;
	private final FollowingService followingService;
	private final ApplicationEventPublisher publisher;

	@Value("${resources.images.urlPrefix}")
	private String urlPrefix;

	@Override
	public Long countUnreadNotificationsByUser(Long userId) {
		User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("Позьзователь с идентификатором: " + userId + " не существует"));
		List<Notification> notifications = notificationRepository.findTop100ByUserAndReadTimeIsNullOrderByCreateTimeDesc(user);
		Predicate<Notification> notificationPredicate = n -> (n.getType().equals(Notification.NotificationType.NOTIFICATION));
		return notifications.stream().filter(notificationPredicate).count();
	}

	@Override
	public List<NotificationView> findNewsByUserAndPresentAsView(Long userId, boolean isPrivate, TimeZone nullableTimeZone) {
		boolean isPublic = !isPrivate;
		User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("Позьзователь с идентификатором: " + userId + " не существует"));
		List<Notification> notifications = notificationRepository.findTop100ByUserOrderByCreateTimeDesc(user);
		Predicate<Notification> notificationPredicate = n -> (isPublic == user.equals(n.getInitiator().orElse(null)) && n.getType().equals(Notification.NotificationType.NEWS));
		return of(nullableTimeZone, notificationPredicate, notifications);
	}

	@Override
	public List<NotificationView> findNotificationsByUserAndPresentAsView(Long userId, TimeZone nullableTimeZone) {
		User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("Позьзователь с идентификатором: " + userId + " не существует"));
		List<Notification> notifications = notificationRepository.findTop100ByUserAndReadTimeIsNullOrderByCreateTimeDesc(user); // не прочитанные пользователем
		notifications.addAll(notificationRepository.findTop100ByUserAndReadTimeIsNotNullOrderByCreateTimeDesc(user)); // прочитанные пользователем
		Predicate<Notification> notificationPredicate = n -> (n.getType().equals(Notification.NotificationType.NOTIFICATION));
		return of(nullableTimeZone, notificationPredicate, notifications);
	}

	private List<NotificationView> of(TimeZone nullableTimeZone, Predicate<Notification> notificationPredicate, List<Notification> notifications) {
		return notifications.stream()
					.filter(notificationPredicate)
					.map(n -> of(n, true, nullableTimeZone)).collect(Collectors.toList());
	}

	public NotificationView of(Notification n, boolean needImageOfTargetObject, TimeZone nullableTimeZone) {
		return new NotificationView(
				n.getId(),
				n.getInitiator().map(User::getId).orElse(null),
				n.getInitiator().map(User::getNickname).orElse(null),
				n.getInitiator().isPresent()?
						n.getInitiator()
								.map(user -> user.getAvatarPath() == null? "/images/no-photo.jpg" : user.getAvatarPath())
								.orElse(User.noImageUrl)
						: "/images/system-photo.png",
				n.getTargetUser().map(User::getId).orElse(null),
				n.getTargetUser().map(User::getNickname).orElse(null),
				needImageOfTargetObject ? n.getImageOfTargetObject().map(s -> urlPrefix + s).orElse(n.getEmptyImageOfTargetObject()) : n.getEmptyImageOfTargetObject(),
				n.getUrlOfTargetObject(),
				n.getBaseMessage(),
				n.getCreateTime(),
				n.getReadTime(),
				n.formattedCreateTime(nullableTimeZone),
				n.getFullMessageText()
		);
	}

	@Override
	public boolean read(Long notificationId) {
		Notification notification = (Notification) notificationRepository.findOne(notificationId);
		if(notification.isRead()) {
			return false;
		}
		notification.setReadTime(ZonedDateTime.now());
		notificationRepository.save(notification);
		return true;
	}

	@Override
	@Async
	public void create(Notification notification) {
		notification.setCreateTime(ZonedDateTime.now());
		notificationRepository.save(notification);
		if (notification.getType().equals(Notification.NotificationType.NOTIFICATION)) {
			publisher.publishEvent(new NotificationTransportEvent(notification));
		}
	}

	@Override
	@Async
	public void create(NotificationPackage<? extends Notification> notificationPackage) {
		followingService.getFollowers(notificationPackage.getUser()).forEach(f -> {
			Notification notification = notificationPackage.getTemplateNotification();
			jpaContext.getEntityManagerByManagedType(notification.getClass()).detach(notification);
			notification.setId(null);
			notification.setUser(f);
			create(notification);
		});
	}

	@Override
	@Transactional
    public void readAll(User u) { notificationRepository.readAll(u); }
}