package su.reddot.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import su.reddot.infrastructure.security.token.EmailRestAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vitaliy Khludeev on 18.04.17.
 */
@Slf4j
public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public RestAuthenticationFilter(AuthenticationManager authenticationManager) {
		super("/mobile/api/**");
		super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/mobile/api/**"));
		setAuthenticationManager(authenticationManager);
		setAuthenticationSuccessHandler((request, response, authentication) -> {

		});
		setAuthenticationFailureHandler((request, response, exception) -> {
			log.warn(exception.getMessage(), exception);
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{}");
			response.getWriter().flush();
			response.getWriter().close();
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String emailParameterName = "email";
		String passwordParameterName = "password";
		String email = request.getParameter(emailParameterName);
		String password = request.getParameter(passwordParameterName);
		if(email == null) {
			throw new AuthenticationServiceException("Email or password is incorrect");
		}
		Authentication authentication = getAuthenticationManager().authenticate(new EmailRestAuthenticationToken(email, password, null));
		if (authentication == null) {
			throw new AuthenticationServiceException("User not found");
		}
		return authentication;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}
}