package su.reddot.domain.dao.following;

import su.reddot.domain.model.user.User;

/** Подписчик - кто подписан на меня */
public interface FollowerProjection {

    User getFollower();

}
