package su.reddot.domain.service.following;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.user.User;

import java.util.List;

public interface FollowingService {

	List<User> getFollowers(User u);

	int getFollowersCount(User u);

	List<User> getFollowings(User u);

	int getFollowingsCount(User u);

	boolean isFollowingExists(User follower, User following);

	boolean follow(User follower, User following);

	boolean unfollow(User follower, User following);

	void toggle(User follower, User following);

	/** Данные о подписчике / подписке */
	@Getter @Setter @Accessors(chain = true)
	class FollowRelated {

		private long id;

		/** Отображаемое всем имя пользователя.
		 * @apiNote не может быть пустым. */
		private String nickname;

		/** Ссылка на изображение профиля. Ссылка указывается относительно хоста.
		 * (точнее относительно контекста сервлета, но он по-умолчанию является корневым: /)
		 * @apiNote может быть null, если пользователь не установил изображение в своем профиле. */
		private String avatarPath;

		/** Количество подписок пользователя
		 **/
		private int followersCount;

		/**
		 * Количество товаров, опубликованных <b>на данный момент</b>.
		 * То есть число товаров, которые можно купить именно сейчас
		 * (если пользователь опубликовал 10 товаров, затем продал 9 из них,
		 * то это свойство будет равно 1).
		 */
		private int availableProductsCount;

		/** * Пользователь может подписаться на этого пользователя. */
		private boolean followingAvailable;

		/** Пользователь может отписаться от этого пользователя */
		private boolean unfollowingAvailable;
	}

	GetBuilder getFor(User u);

	interface GetBuilder {


	    /** Получить общее число выбранных объектов */
	    int count();

		/** Получить выбранные объекты */
	    List<FollowRelated> build();

		/** Получить подписчиков - тех пользователей, которые подписались на данного пользователя. */
	    GetBuilder followers();

		/**
		 * <p>Получить подписки - те пользователи, на которых подписался данный пользователь.</p>
		 * <p><a href="https://en.wiktionary.org/wiki/followee">ee - суффикс, который указывает на объекта действия</a></p>
		 */
	    GetBuilder followees();

	    /** Может ли подписаться / отписаться данный пользователь от найденных подписчиков. */
		GetBuilder withFollowAvailability(User u);
	}
}
