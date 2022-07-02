package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

@ControllerAdvice(basePackageClasses = DefaultAdminAdvice.class)
@RequiredArgsConstructor
public class DefaultAdminAdvice {
    private final UserService userService;

    @ModelAttribute
    User getUser(UserIdAuthenticationToken t) {
        return t == null? null : userService.getUserById(t.getUserId()).get();
    }
}
