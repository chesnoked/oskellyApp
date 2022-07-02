package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.Following;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 22.09.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class FollowingNotification extends Notification {

	@ManyToOne
	@NotNull
	private Following following;

	@Override
	public Optional<User> getInitiator() {
		return Optional.of(following.getFollower());
	}

	@Override
	public Optional<User> getTargetUser() {
		return Optional.of(following.getFollowing());
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(following.getFollowing().getAvatarPath());
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return User.noImageUrl;
	}

	@Override
	public String getUrlOfTargetObject() {
		return String.format("/profile/%s", following.getFollowing().getId());
	}

	@Override
	public String getBaseMessage() {
		return "подписался(-ась) на новости";
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NEWS;
	}
}
