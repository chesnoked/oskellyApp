package su.reddot.domain.dao.following;

import su.reddot.domain.model.user.User;

/** Подписка - тот пользователь, на которого подписан я. */
public interface FolloweeProjection {

    User getFollowing();
}
