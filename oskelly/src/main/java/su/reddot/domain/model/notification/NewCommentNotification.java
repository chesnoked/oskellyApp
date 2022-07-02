package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.Comment;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Entity
@Getter @Setter @Accessors(chain = true)
public class NewCommentNotification extends Notification {

	@ManyToOne
	@NotNull
	private Comment comment;

	@Override
	public Optional<User> getInitiator() { return Optional.of(comment.getPublisher()); }

	@Override
	public Optional<User> getTargetUser() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(comment.getProduct().getImagePreview());
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return null;
	}

	@Override
	public String getUrlOfTargetObject() {
		return String.format("/products/%s", comment.getProduct().getId());
	}

	@Override
	public String getBaseMessage() {

		Product commentedProduct = comment.getProduct();

		return String.format("добавил новый комментарий к вашему товару %s %s",
				commentedProduct.getBrand().getName(),
				commentedProduct.getDisplayName());
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NOTIFICATION;
	}
}
