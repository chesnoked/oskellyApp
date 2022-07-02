package su.reddot.domain.dao.like;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.like.Like;
import su.reddot.domain.model.user.User;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 21.09.17.
 */
public interface BaseLikeRepository extends JpaRepository<Like, Long> {

	List<Like> findTop100ByUserOrderByIdDesc(User u);

	int countAllByUser(User u);
}
