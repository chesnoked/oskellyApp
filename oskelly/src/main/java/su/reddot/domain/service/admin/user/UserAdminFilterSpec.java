package su.reddot.domain.service.admin.user;

import lombok.Value;

@Value
public class UserAdminFilterSpec {
	boolean isPro;
	boolean isSeller;
	boolean isNew;
	int page;
	int limit;
}
