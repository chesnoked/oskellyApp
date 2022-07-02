package su.reddot.infrastructure.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Base64.getUrlEncoder;

public class LoginRedirectionEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException) throws IOException, ServletException {
		String requestURI = request.getRequestURI();
		String base64URI = new String(getUrlEncoder().encode(requestURI.getBytes()));
		response.sendRedirect("/login?from=" + base64URI);
	}
}

