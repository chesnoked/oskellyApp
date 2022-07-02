package su.reddot.domain.service.admin.user.userpage.view;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProfileView {
	private String sex;
	private String birthday;
	private String sellerCity;

	/**
	 * Ушел в отпуск или нет
	 */
	private boolean active;

	private String deliveryCity;
	private String deliveryPostcode;
	private String deliveryAddress;

}
