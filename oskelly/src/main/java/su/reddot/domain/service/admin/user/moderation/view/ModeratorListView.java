package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Value;

import java.util.List;

@Value
public class ModeratorListView {

	/*
		Перечисление всех модераторских прав в системе
	 */
	List<String> authorities;
	List<ModeratorView> list;
}
