package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.WishList;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Если я подписан на пользователя, то когда он добавляет товар в WishList мне
 * должна приходить соответствующая нотификация
 * @author Vitaliy Khludeev on 20.12.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class AddToWishListNotification extends Notification {

	@ManyToOne
	@NotNull
	private WishList wishList;

	@Override
	public Optional<User> getInitiator() {
		return Optional.of(wishList.getUser());
	}

	@Override
	public Optional<User> getTargetUser() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(wishList.getProduct().getImagePreview());
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return null;
	}

	@Override
	public String getUrlOfTargetObject() {
		return wishList.getProduct().getUrl();
	}

	@Override
	public String getBaseMessage() {
		return "добавил(-а) товар в вишлист";
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NEWS;
	}
}
