package su.reddot.domain.dao.following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.Following;
import su.reddot.domain.model.user.User;

import java.util.List;

public interface FollowingRepository extends JpaRepository<Following, Long> {

	/**
	 * Подписан ли пользователь {@code follower} на пользователя {@code following}
	 * @param follower подписчик
	 * @param following подписка
	 * @return true, если пользователь подписан на другого пользователя, иначе false
	 */
	boolean existsByFollowerAndFollowing(User follower, User following);

	/**
	 * Получить список подписчиков пользователя.
	 * @param u пользователь
	 * @return подписчики пользователя
	 */
	@Query("select f from Following f where f.following = ?1")
	List<FollowerProjection> findFollowers(User u);

	/** Число подписчиков */
	int countFollowersByFollowing(User u);

	/**
	 * Получить список подписок пользователя.
	 * @param u пользователь
	 * @return подписки пользователя
	 */
	@Query("select f from Following f where f.follower = ?1")
	List<FolloweeProjection> findFollowees(User u);

	/** Число подписок */
	int countFolloweesByFollower(User u);

	//---

	Following findByFollowerAndFollowing(User follower, User following);

	@Query("select f.follower from Following f where f.following = ?1 and f.follower <> f.following")
	List<User> findByFollowing(User u);

	@Query("select count(f) from Following f where f.following = ?1 and f.follower <> f.following")
	int countByFollowing(User u);

	@Query("select f.following from Following f where f.follower = ?1 and f.follower <> f.following")
	List<User> findByFollower(User u);

	@Query("select count(f) from Following f where f.follower = ?1 and f.follower <> f.following")
	int countByFollower(User u);
}
