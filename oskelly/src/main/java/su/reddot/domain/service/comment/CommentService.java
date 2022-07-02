package su.reddot.domain.service.comment;

import java.util.List;
import java.util.TimeZone;

/**
 * @author Vitaliy Khludeev on 29.08.17.
 */
public interface CommentService {

	List<CommentView> getComments(Long productId, TimeZone timezone);

	CommentView publishComment(String text, Long userId, Long productId, TimeZone timezone);
}