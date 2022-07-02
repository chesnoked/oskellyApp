package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;
import java.util.Optional;

@ControllerAdvice(basePackages = "su.reddot.presentation.controller")
@RequiredArgsConstructor
@Slf4j
public class DefaultControllerAdvice {

    private final CategoryService     categoryService;
    private final UserService         userService;
    private final NotificationService notificationService;

    private CartService cartService;

    @Autowired
    public void setCartService(@Qualifier("defaultCartService") CartService cs) {
        cartService = cs;
    }

    /**
     * Структура каталога будет отображаться практически на всех страницах сайта.
     * Поэтому атрибут модели должен быть доступен во всех html шаблонах.
     */
    @ModelAttribute("catalog")
    public List<CatalogCategory> addCatalog() {
        return categoryService.getEntireCatalog();
    }

    /**
     * @return размер корзины, если пользователь - гость или
     * зарегистрированный пользователь, иначе null.
     */
    @ModelAttribute("cartSize")
    public Long cartSize(UserIdAuthenticationToken token, @CookieValue(required = false) String osk) {

        Long userId = token != null? token.getUserId() : null;
        if (userId != null) {

            Optional<User> user = userService.getUserById(userId);
            return user.map(cartService::getCartSize)
                    .orElse(null);
        }

        return osk != null? cartService.getCartSize(osk) : null;
    }

    @ModelAttribute("notifications")
    public Long notificationsCount(UserIdAuthenticationToken token){
        Long userId = token != null? token.getUserId() : null;
        if (userId == null) { return null; }

        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) { return null; }

        return notificationService.countUnreadNotificationsByUser(userId);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String notFoundHandler(){
        return "error/404";
    }
}
