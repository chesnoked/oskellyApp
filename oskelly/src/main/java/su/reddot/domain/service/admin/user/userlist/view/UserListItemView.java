package su.reddot.domain.service.admin.user.userlist.view;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserListItemView {
	private Long id;
	private String email;
	private String nickname;

	/**
	 * ФИО или название
	 */
	private String name;

	private String phone;

	private String statuses;

	private int publishedProducts;
	private int moderatedProducts;

	private int orders;
	private int orderItems;
}
