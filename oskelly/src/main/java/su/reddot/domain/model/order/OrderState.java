package su.reddot.domain.model.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderState {

	CREATED("Новый", true),

	/**
	 * Если пользователь в течение заданного времени не оплачивает заказ,
	 * то система такой заказ отменяет, снимая с товаров заказа бронирование.
	 * Иначе товары будут забронированы вечно,
	 * и другие пользователи не смогут их купить.
	 */
	CANCELED("Отменен", true),

	/**
	 * Пользователь инициировал оплату, но еще не оплатил заказ.
	 */
	HOLD_PROCESSING("Платеж выполняется", true),

	HOLD_ERROR("Ошибка при оплате", true),

	/**
	 * Успешное резервирование средств на счету клиента
	 */
	HOLD("Средства на оплату успешно зарезервированы", false),
	HOLD_COMPLETED("Оплачен успешно", false),
	REFUND("Возврат средств", false), // TODO вменяемое описание

	COMPLETION_PROCESSING("?", false),
	COMPLETED("?", false);

	/**
	 * Статус заказа в понятной человеку форме
	 */
	private final String description;

	private final boolean orderIsNotPayedYet;
}
