package su.reddot.infrastructure.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.UserAuthorityBindingRepository;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.token.EmailRestAuthenticationToken;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 18.04.17.
 */
@Component
public class EmailRestAuthenticationProvider implements AuthenticationProvider {

	private final MessageDigestPasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserAuthorityBindingRepository userAuthorityBindingRepository;

	@Autowired
	public EmailRestAuthenticationProvider(MessageDigestPasswordEncoder passwordEncoder, UserRepository userRepository, UserAuthorityBindingRepository userAuthorityBindingRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userAuthorityBindingRepository = userAuthorityBindingRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		EmailRestAuthenticationToken token = (EmailRestAuthenticationToken) authentication;
		User user = userRepository.findByEmail((String) token.getPrincipal());
		if (user == null) {
			throw new UsernameNotFoundException("Неправильный email");
		}

		String encodePassword = passwordEncoder.encodePassword((String) token.getCredentials(), token.getPrincipal());
		if (!encodePassword.equals(user.getApiHashedPassword())) {
			throw new BadCredentialsException("Неправильный пароль");
		} else {
			List<Authority> authorities = userAuthorityBindingRepository.findByUser(user);
			UserIdAuthenticationToken userIdAuthenticationToken = new UserIdAuthenticationToken(user.getId(), authorities);
			userIdAuthenticationToken.setAuthenticated(true);
			return userIdAuthenticationToken;
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return EmailRestAuthenticationToken.class.isAssignableFrom(aClass);
	}
}
