package su.reddot.domain.service.admin.user.userlist.view;

import lombok.Value;

import java.util.List;

@Value
public class UserListView {
	private List<UserListItemView> list;
	private int page;
	private int count;
}
