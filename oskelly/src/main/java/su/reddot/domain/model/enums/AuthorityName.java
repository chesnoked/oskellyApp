package su.reddot.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthorityName {

	ADMIN("Администратор сайта"),

	PRODUCT_MODERATION("Модерация товаров"),

	USER_MODERATION("Управление пользователями"),
	AUTHORITY_MODERATION("Управление правами"),

	CONTENT_CREATE("Публикация информации"),
	CONTENT_DELETE("Удаление опубликованной информации"),

	ORDER_MODERATION("Обслуживание заказов"),

	CAN_VIEW_ALL_PRODUCTS("Просмотр всех товаров, кроме черновиков");

	private final String description;
}
