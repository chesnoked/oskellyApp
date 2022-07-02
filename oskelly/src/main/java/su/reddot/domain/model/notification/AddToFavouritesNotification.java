package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.like.Like;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 20.12.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class AddToFavouritesNotification extends Notification {

	@ManyToOne
	@NotNull
	private Like like;

	@Override
	public Optional<User> getInitiator() {
		return Optional.of(like.getUser());
	}

	@Override
	public Optional<User> getTargetUser() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(like.getLikable().getImagePreview());
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return null;
	}

	@Override
	public String getUrlOfTargetObject() {
		return like.getLikable().getUrl();
	}

	@Override
	public String getBaseMessage() {
		return "добавил(-а) " + like.getLikable().getEntityName() + " в фавориты";
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NEWS;
	}
}