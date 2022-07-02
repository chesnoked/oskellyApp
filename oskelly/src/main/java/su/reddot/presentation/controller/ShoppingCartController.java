package su.reddot.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.cart.impl.dto.Cart;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ShoppingCartController {

    private CartService<Cart> cartService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService us) { userService = us; }

    @Autowired
    public void setCartService(@Qualifier("defaultCartService") CartService<Cart> cs) {
        cartService = cs;
    }

    @GetMapping({"/cart", "/checkout"})
    public String getCart(Model m, UserIdAuthenticationToken t,
                          @CookieValue(required = false) String osk,
                          HttpServletRequest r
    ) {
        Cart cart = null;
        if (t != null) {
            User user = userService.getUserById(t.getUserId())
                    .orElseThrow(IllegalArgumentException::new);

            cart = cartService.getCart(user);
        }
        else if (osk != null){
           cart = cartService.getCart(osk);
        }

        m.addAttribute("cart", cart);

        return "order" + r.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    }
}
