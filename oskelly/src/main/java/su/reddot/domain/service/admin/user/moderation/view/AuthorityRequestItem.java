package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Data;

@Data
public class AuthorityRequestItem {
	private Long authorityId;
	private boolean checked;
}
