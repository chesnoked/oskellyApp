package su.reddot.domain.service.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Vitaliy Khludeev on 02.09.17.
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProfileSellerRequest {

	private String firstName;

	private String lastName;

	/** Название компании для юридических лиц */
	private String companyName;

	private String phone;

	private String zipCode;

	private String city;

	private String address;
	private String extensiveAddress;

	/**
	 * Паспорт (физик)
	 */
	private String passport;

	/**
	 * Индивидуальный номер налогоплательщика (физик, ИП)
	 */
	private String INN;

	/**
	 * Основной государственный регистрационный номер индивидульного предпринимателя (ИП)
	 */
	private String OGRNIP;

	/**
	 * Основной государственный регистрационный номер (ООО)
	 */
	private String OGRN;

	/**
	 * Код причины постановки (ООО)
	 */
	private String KPP;

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
