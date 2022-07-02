package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.Comment;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 29.08.17.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findTop100ByProductIdOrderByIdDesc(Long productId);

	List<Comment> findAllByProductIdOrderByPublishTime(Long productId);
}
