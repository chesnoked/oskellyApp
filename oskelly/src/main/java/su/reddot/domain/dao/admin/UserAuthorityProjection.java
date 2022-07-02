package su.reddot.domain.dao.admin;

import su.reddot.domain.model.enums.AuthorityName;

public interface UserAuthorityProjection {

	Long getUserId();

	String getEmail();

	String getNickname();

	String getFirstName();

	String getLastName();

	String getAvatarPath();

	Long getAuthorityId();

	AuthorityName getAuthorityName();
}
