package su.reddot.domain.model.notification.offer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.product.Offer;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Entity
@Getter @Setter @Accessors(chain = true)
public class NewOfferNotification extends Notification {

	@ManyToOne
	@NotNull
	private Offer offer;

	@Override
	public Optional<User> getInitiator() { return Optional.empty(); }

	@Override
	public Optional<User> getTargetUser() { return Optional.empty(); }

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(offer.getProduct().getImagePreview());
	}

	@Override
	public String getEmptyImageOfTargetObject() { return null; }

	@Override
	public String getUrlOfTargetObject() {
		return String.format("/account/offers/%s", offer.getId());
	}

	@Override
	public String getBaseMessage() {

		Product p = offer.getProduct();

		return String.format("Новое предложение цены к Вашему товару %s %s",
				p.getBrand().getName(),
				p.getDisplayName());
	}

	@Override
	public NotificationType getType() { return NotificationType.NOTIFICATION; }

}
