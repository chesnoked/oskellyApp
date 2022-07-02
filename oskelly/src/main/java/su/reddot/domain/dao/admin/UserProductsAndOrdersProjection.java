package su.reddot.domain.dao.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@Getter
public class UserProductsAndOrdersProjection {

	/**
	 * Id пользователя
	 */
	private BigInteger userid;

	/**
	 * псевдоним пользователя
	 */
	private String nickname;

	/**
	 * email пользователя
	 */
	private String email;

	/**
	 * Имя пользователя
	 */
	private String firstname;

	/**
	 * ФИО пользователя
	 */
	private String lastname;

	/**
	 * Номер телефона пользователя
	 */
	private String phone;

	/**
	 * Количество заказов у пользователя
	 */
	private BigInteger orderscount;

	/**
	 * Количество всех вещей во всех заказах пользователя
	 */
	private BigInteger orderitems;

	/*
		Пояснение:
		для нижних столбцом вернется что-то вроде такого:
		(uid:1, products: 10, state: PUBLISHED),
		(uid:1, products:5, state: NEED_MODERATION).
		Все поля для строк с одинаковым uid, за исключением products и state, идентичны.
		Может прийти такое:
		(uid:5, products: 0, state: null). Это значит, что не нашлось ни одной вещи,
		которые были бы хоть в одном из вышеперечисленных состояний.
	 */

	/**
	 * количество вещей пользователя с нижеуказанным статусом
	 */
	private BigInteger products;

	/**
	 * статус вещей для текущей выборки
	 */
	private String productstate;

	private Boolean isPro;
	private Boolean isVip;
	private Boolean isTrusted;
}
