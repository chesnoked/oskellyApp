package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Entity
@Getter @Setter @Accessors(chain = true)
public class OrderStateChangedNotification extends Notification {

	@ManyToOne
	@NotNull
	private Order order;

	@NotNull
	@Enumerated(EnumType.STRING)
	private OrderState newOrderState;

	@Override
	public Optional<User> getInitiator() { return Optional.empty(); }

	@Override
	public Optional<User> getTargetUser() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getImageOfTargetObject() {
		return Optional.empty();
	}

	@Override
	public String getEmptyImageOfTargetObject() {
		return null;
	}

	@Override
	public String getUrlOfTargetObject() { return "/account/orders"; }

	@Override
	public String getBaseMessage() {
		String message;
		if (newOrderState == OrderState.HOLD) {
			message = String.format("Поступила оплата по заказу %s. " +
					"Ожидаем подтверждения продажи от продавцов", order.getId());
		}
		else {
			message = String.format("Новый статус заказа %s: %s", order.getId(),
					newOrderState.getDescription());
		}

		return message;
	}

	@Override
	public NotificationType getType() {
		return NotificationType.NOTIFICATION;
	}
}