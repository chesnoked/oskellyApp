package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MainPageController {

    @GetMapping
    String getMasterPage(UserIdAuthenticationToken token) {

        /* любой пользователь, имеющий хотя бы одно (любое) право доступа,
        * может попасть на главную страницу
        */
        if (token == null || !token.hasAnyAuthority()) {
            throw new AccessDeniedException(null);
        }

        return "admin/main";
    }
}
