package su.reddot.infrastructure.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.UserAuthorityBindingRepository;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static su.reddot.domain.model.user.User.UserType.SIMPLE_USER;

/**
 * @author Vitaliy Khludeev on 09.09.17.
 */
@Component
@RequiredArgsConstructor
public class OauthAuthenticationProvider implements AuthenticationProvider {

	private final UserAuthorityBindingRepository userAuthorityBindingRepository;
	private final UserRepository userRepository;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		OAuth2Authentication oauth = (OAuth2Authentication) authentication;
		String facebookId = oauth.getUserAuthentication().getPrincipal().toString();
		Map<String, String> details = (Map<String, String>) oauth.getUserAuthentication().getDetails();
		User user = userRepository.findByFacebookId(facebookId);
		if (user == null) {
			user = new User()
					.setFacebookId(facebookId)
					.setRegistrationTime(ZonedDateTime.now())
					.setNickname(details.get("name"))
					.setUserType(SIMPLE_USER);
			userRepository.save(user);

		}

		List<Authority> authorities = userAuthorityBindingRepository.findByUser(user);
		UserIdAuthenticationToken userIdAuthenticationToken = new UserIdAuthenticationToken(user.getId(), authorities);
		userIdAuthenticationToken.setAuthenticated(true);
		return userIdAuthenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2Authentication.class.isAssignableFrom(authentication);	}
}
