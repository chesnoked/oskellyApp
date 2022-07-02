package su.reddot.infrastructure.security.persistentsession;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.UserAuthorityBindingRepository;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

import static org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

@Service
@RequiredArgsConstructor
public class PersistentSessionServiceImpl implements PersistentSessionService {

	private final FindByIndexNameSessionRepository persistentSessionRepository;
	private final UserRepository userRepository;
	private final UserAuthorityBindingRepository authorityBindingRepository;

	@Override
	public void updateUserSecurityContext(Long userId) {
		for (Object o : persistentSessionRepository
				.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, String.valueOf(userId))
				.values()) {
			Session session = (Session) o;
			SecurityContext securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
			if (securityContext == null) {
				continue;
			}

			Authentication oldAuthentication = securityContext.getAuthentication();
			User one = userRepository.findOne(userId);
			if (one == null) {
				continue;
			}
			List<Authority> byUser = authorityBindingRepository.findByUser(one);
			UserIdAuthenticationToken authenticationToken = new UserIdAuthenticationToken(one.getId(), byUser);
			authenticationToken.setAuthenticated(oldAuthentication.isAuthenticated());

			securityContext.setAuthentication(authenticationToken);
			session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
			persistentSessionRepository.save(session);
		}
	}
}
