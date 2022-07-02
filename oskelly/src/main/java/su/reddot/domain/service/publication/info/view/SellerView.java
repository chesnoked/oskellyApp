package su.reddot.domain.service.publication.info.view;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SellerView {

	/**
	 * Имя
	 */
	private String firstName;

	/**
	 * Фамилия
	 */
	private String lastName;

	/**
	 * Почтовый индекс
	 */
	private String postcode;

	/**
	 * Город, в котором продавец продает товар
	 */
	private String city;

	/**
	 * Адрес, по которому у продавца надо забрать вещь
	 */
	private String address;

	/**
	 * Номер продавца
	 */
	private String phone;
}
