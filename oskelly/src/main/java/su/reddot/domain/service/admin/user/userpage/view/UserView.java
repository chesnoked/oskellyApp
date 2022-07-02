package su.reddot.domain.service.admin.user.userpage.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserView {

	private Long userId;
	private String nickname;
	private String phone;
	private String email;
	private boolean emailConfirmed;
	private String status;

	/**
	 * Фио либо название
	 */
	private String name;

	private String registrationDate;

	private ProfileView profile;
	private SellerView seller;

	private String avatar;

}
