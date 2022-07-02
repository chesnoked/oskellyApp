package su.reddot.domain.service.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * <b>Класс используется в</b>
 * {@link su.reddot.presentation.mobile.api.v1.CommentRestControllerV1}
 * @author Vitaliy Khludeev on 29.08.17.
 */
@Getter
@RequiredArgsConstructor
public class CommentView {
	private final Long id;
	private final String text;
	private final String publishTime;
	private final ZonedDateTime publishZonedDateTime;
	private final Long userId;
	private final String user;
	private final String avatar;
}
