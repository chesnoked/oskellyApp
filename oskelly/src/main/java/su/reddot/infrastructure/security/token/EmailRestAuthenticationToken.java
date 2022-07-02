package su.reddot.infrastructure.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Vitaliy Khludeev on 18.04.17.
 */
public class EmailRestAuthenticationToken extends AbstractAuthenticationToken {
	private final String email;
	private final String password;

	public EmailRestAuthenticationToken(String email, String password, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.email = email;
		this.password = password;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public Object getPrincipal() {
		return email;
	}
}
