package su.reddot.domain.service.like;

import su.reddot.domain.model.like.Likeable;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.like.type.ToggleResult;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 14.09.17.
 */
public interface LikeService {

	/**
	 * Сохранянт лайк в БД
	 * @param l
	 * @param u
	 * @return результат выполнения: true - если лайка не было и он был сохранен, false - если лайк уже был в БД
	 */
	boolean like(Likeable l, User u);

	/**
	 * Удаляет лайк из БД
	 * @param l
	 * @param u
	 * @return результат выполнения: true - если лайк был и он был удален, false - лайка не было в БД в БД
	 */
	boolean dislike(Likeable l, User u);

	/** Лайкнуть, если лайка еще нет, или дизлайкнуть, если лайк уже есть. */
	ToggleResult toggle(Likeable l, User u);

	/**
	 * Лайкнул ли пользователь сущность Likeable
	 * @param l
	 * @param u
	 * @return
	 */
	boolean doesUserLike(Likeable l, User u);

	/**
	 * Число лайков, поставленных к сущности Likeable
	 * @param l
	 * @return
	 */
	int countByLikeable(Likeable l);

	/**
	 * Число всех лайков на объекты типа clazz пользователя
	 * @param u
	 * @param clazz
	 * @return
	 */
	int countByAllUserLikeables(User u, Class<? extends Likeable> clazz);

	/**
	 * получаем все отлайканные пользователем объекты
	 * @param u
	 * @return
	 */
	List<Likeable> getLikeablesWhichUserLiked(User u);

	List<? extends Likeable> getLikeablesWhichUserLiked(User u, Class<? extends Likeable> cls);

	/**
	 * получаем все лайки НА объекты пользователя ОТ ДРУГИХ пользоватлей
	 * @param u
	 * @return
	 */
	int countByUser(User u);
}
