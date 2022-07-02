package su.reddot.domain.dao.like;

import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.like.Likeable;
import su.reddot.domain.model.like.ProductLike;
import su.reddot.domain.model.user.User;

/**
 * @author Vitaliy Khludeev on 14.09.17.
 */
public interface ProductLikeRepository extends LikeRepository<ProductLike> {

	@Override
	@Query("select l from ProductLike l where l.product = ?1 and l.user = ?2")
	ProductLike findByLikableAndUser(Likeable l, User u);

	@Override
	@Query("select count(l) from ProductLike l where l.product = ?1")
	int countByLikable(Likeable l);

	@Override
	int countByUser(User u);

	@Override
	@Query("select count(l) from ProductLike l join l.product p where p.seller = ?1")
	int countByAllUserLikeables(User u);

}
