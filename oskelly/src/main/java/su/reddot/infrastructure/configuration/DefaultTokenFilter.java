package su.reddot.infrastructure.configuration;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/** Добавляет токен анонимному пользователю. */
@Component
public class DefaultTokenFilter implements Filter {

    private final static int ONE_MONTH = 60 * 60 * 24 * 30;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean guestCookieIsSet = req.getCookies() != null
                && Arrays.stream(req.getCookies())
                .anyMatch(cookie -> "osk".equals(cookie.getName()));

        if (!guestCookieIsSet) {
            Cookie osk = new Cookie("osk", UUID.randomUUID().toString());
            osk.setMaxAge(ONE_MONTH);
            osk.setPath("/");
            res.addCookie(osk);

            boolean requestIsGuestSensitive = req.getRequestURI() != null && (
                    req.getRequestURI().startsWith("/products/")
                    || req.getRequestURI().startsWith("/cart")
                    || req.getRequestURI().startsWith("/checkout"));

            if (requestIsGuestSensitive) { res.sendRedirect(req.getRequestURI()); return; }
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
