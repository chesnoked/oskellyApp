package su.reddot.infrastructure.configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vitaliy Khludeev on 09.09.17.
 */
public class CustomOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

	CustomOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler) {
		super(defaultFilterProcessesUrl);
		setAuthenticationManager(authenticationManager);
		setAuthenticationSuccessHandler(authenticationSuccessHandler);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		Authentication oauth = super.attemptAuthentication(request, response);
		return getAuthenticationManager().authenticate(oauth);
	}
}
