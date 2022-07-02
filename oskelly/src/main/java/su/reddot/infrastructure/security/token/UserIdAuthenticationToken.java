package su.reddot.infrastructure.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import su.reddot.domain.model.enums.AuthorityName;

import java.util.Collection;

public class UserIdAuthenticationToken extends AbstractAuthenticationToken {

	private final Long userId;

	public UserIdAuthenticationToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.userId = userId;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.userId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public boolean hasAuthority(AuthorityName authorityName) {
		return getAuthorities().stream()
				.filter(a -> a.getAuthority().equals(authorityName.name()))
				.findFirst().isPresent();
	}

	public boolean hasAnyAuthority() {
		return getAuthorities().size() > 0;
	}
}
