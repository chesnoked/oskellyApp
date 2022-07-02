package su.reddot.domain.model.user;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Реквизиты на оплату (одинаковые для любого типа пользователя)
 */
@Embeddable
@Data
public class PaymentRequisite {

	/**
	 * Банковский идентификационный код
	 */
	private String BIK;

	/**
	 * Корреспондентский счет
	 */
	private String correspondentAccount;

	/**
	 * Лицевой/Расчетный счет
	 */
	private String paymentAccount;
}
