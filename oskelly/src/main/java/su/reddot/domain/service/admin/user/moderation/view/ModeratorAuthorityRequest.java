package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Data;

import java.util.List;

@Data
public class ModeratorAuthorityRequest {
	private Long userId;
	private List<AuthorityRequestItem> authorities;
}
