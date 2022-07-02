package su.reddot.infrastructure.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.user.User;

@RequiredArgsConstructor
@Getter
public class CustomAuthenticationSuccessEvent {

	private final User authenticatedUser;

	private final String guestToken;
}
