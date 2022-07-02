package su.reddot.infrastructure.configuration;

import org.springframework.stereotype.Component;
import su.reddot.domain.model.AttractionOfTraffic;
import su.reddot.domain.service.attractionOfTraffic.AttractionOfTrafficService;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * @author Vitaliy Khludeev on 08.10.17.
 */
@Component
public class AttractionOfTrafficFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		Arrays.asList(AttractionOfTraffic.Type.values()).forEach(t -> process(req, res, t));
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {

	}

	private void process(HttpServletRequest req, HttpServletResponse res, AttractionOfTraffic.Type type) {
		if(
				req.getParameter("utm_source") != null &&
				req.getParameter("utm_source").equals(type.getCookieName()) &&
				req.getParameter(type.getRequestParam()) != null
		) {
			String cookieValue = req.getParameter(type.getRequestParam());
			Cookie cookie = new Cookie(type.getCookieName(), cookieValue);
			cookie.setMaxAge(3600 * 24 * type.getDaysBeforeExpire());
			cookie.setPath("/");
			res.addCookie(cookie);

			String cookieExpireValue = ZonedDateTime.now().plusDays(type.getDaysBeforeExpire()).format(AttractionOfTrafficService.DATE_TIME_FORMATTER);
			Cookie cookieExpire = new Cookie(type.getCookieExpireTimeName(), cookieExpireValue);
			cookieExpire.setMaxAge(3600 * 24 * type.getDaysBeforeExpire());
			cookieExpire.setPath("/");
			res.addCookie(cookieExpire);
		}
	}
}
