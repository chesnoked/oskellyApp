package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 18.09.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class SaleNeedConfirmationNotification extends Notification {

	@ManyToOne
	@NotNull
	private ProductItem productItem;

	/**
	 * Вообще, инициатор - это покупатель
	 * Но, продавец не должен знать кто у него купил вещь
	 * @see ProductItem#getEffectiveOrder()
	 * @return Optional.empty
	 */
	@Override
	public Optional<User> getInitiator() {
		return Optional.empty();
	}

	@Override
	public Optional<User> getTargetUser() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.ofNullable(productItem.getProduct().getImagePreview());
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return null;
	}

	@Override
	public String getUrlOfTargetObject() {
		return String.format("/account/products/items/%s/sale-confirmation", productItem.getId());
	}

	@Override
	public String getBaseMessage() {
		return String.format("Ваш товар \"%s\" готовы купить! Подтвердите сделку в личном кабинете", productItem.getProduct().getDisplayName());
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NOTIFICATION;
	}
}
