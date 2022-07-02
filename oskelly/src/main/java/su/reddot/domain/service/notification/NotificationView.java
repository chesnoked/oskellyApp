package su.reddot.domain.service.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 20.12.17.
 */
@RequiredArgsConstructor
@Getter
public class NotificationView {
	private final Long id;
	private final Long initiatorId;
	private final String initiatorNickname;
	private final String initiatorAvatar;
	private final Long targetUserId;
	private final String targetUserNickname;
	private final String imageOfTargetObject;
	private final String urlOfTargetObject;
	private final String baseMessage;
	private final ZonedDateTime createTime;
	private final ZonedDateTime readTime;
	private final String prettyCreateTime;
	private final String fullMessage;
}
