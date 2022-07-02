package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Value;

@Value
public class AddModeratorResponseItem {
	Long id;
	String name;
	String email;
}
