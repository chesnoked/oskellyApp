package su.reddot.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.attractionOfTraffic.AttractionOfTrafficService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Vitaliy Khludeev on 09.10.17.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;

	private final AttractionOfTrafficService attractionOfTrafficService;

	private final ApplicationEventPublisher pub;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		UserIdAuthenticationToken token = (UserIdAuthenticationToken) authentication;
		User user = userService.getUserById(token.getUserId()).get();


		Cookie[] cookies = request.getCookies();

		/* может быть null если в запросе нет ни одного значения куки */
		if (cookies == null) return;

        attractionOfTrafficService.saveIfNeed(user, Arrays.asList(cookies));

        Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("osk"))
                .findFirst()
                .ifPresent(cookie -> pub.publishEvent(
                        new CustomAuthenticationSuccessEvent(
                                user, cookie.getValue())
                        ));
	}
}

