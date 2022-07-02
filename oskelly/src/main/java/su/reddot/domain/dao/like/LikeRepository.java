package su.reddot.domain.dao.like;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import su.reddot.domain.model.like.Like;
import su.reddot.domain.model.like.Likeable;
import su.reddot.domain.model.user.User;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 14.09.17.
 */
@NoRepositoryBean
public interface LikeRepository<T extends Like> extends CrudRepository<T, Long> {

	T findByLikableAndUser(Likeable l, User u);

	int countByLikable(Likeable l);

	int countByAllUserLikeables(User u);

	int countByUser(User u);

	List<T> findTop100ByUserOrderByIdDesc(User u);
}
