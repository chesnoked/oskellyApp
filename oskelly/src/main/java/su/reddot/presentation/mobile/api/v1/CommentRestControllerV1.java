package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.comment.CommentService;
import su.reddot.domain.service.comment.CommentView;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;
import java.util.TimeZone;

/**
 * @author Vitaliy Khludeev on 29.08.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/comments")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class CommentRestControllerV1 {

	private final CommentService commentService;

	private final ApplicationEventPublisher pub;

	@GetMapping(value = "/product/{productId}")
	public List<CommentView> getComments(@PathVariable Long productId, TimeZone timezone) {
		return commentService.getComments(productId, timezone);
	}

	@PostMapping(value = "/product")
	public CommentView publishComment(
			@RequestParam Long productId,
			@RequestParam String text,
			UserIdAuthenticationToken token,
			TimeZone timezone
	) {
		CommentView comment = commentService.publishComment(text, token.getUserId(), productId, timezone);

		return comment;
	}
}
