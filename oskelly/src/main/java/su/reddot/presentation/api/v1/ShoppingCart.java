package su.reddot.presentation.api.v1;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.cart.impl.dto.Cart;
import su.reddot.domain.service.cart.impl.dto.GuestOrderCreationParams;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingCart {

    private       CartService<Cart> cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    private final SpringTemplateEngine templateEngine;

    private final ServletContext ctx;

    // FIXME
    @Value("${acquirer.mdm-pay.web-endpoint}")
    private String acquirerWebEndpoint;

    @Autowired
    public void setCartService(@Qualifier("defaultCartService") CartService<Cart> cs) {
        cartService = cs;
    }

    @GetMapping
    public EffectiveCart getCart(UserIdAuthenticationToken token,
                          @CookieValue(required = false) String osk,
                          HttpServletRequest req,
                          HttpServletResponse resp) {

        Optional<User> userIfAny = assertUserValidity(token, osk);

        Cart nullableCart = userIfAny.isPresent()? cartService.getCart(userIfAny.get())
                : cartService.getCart(osk);

        String renderedCart = renderCart(nullableCart, req, resp);

        return new EffectiveCart(renderedCart, nullableCart == null? 0 : nullableCart.getSize());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(Long productId, Long sizeId,
                              @CookieValue(required = false) String osk,
                              UserIdAuthenticationToken token)
            throws ProductNotFoundException, ProductCanNotBeAddedToCartException {

        Optional<User> userIfAny = assertUserValidity(token, osk);
        if (userIfAny.isPresent()) {
           cartService.addItem(productId, sizeId, userIfAny.get());
        }
        else {
            cartService.addItem(productId, sizeId, osk);
        }

        return ok().build();
    }

    /**
     * Удалить элемент из корзины
     * @return актуальное состояние корзины
     */
    @DeleteMapping("/items/{id}")
    public EffectiveCart removeItem(@PathVariable Long id,
                                    @RequestParam(required = false) boolean fromCheckout,
                                    UserIdAuthenticationToken token,
                                    @CookieValue(required = false) String osk,
                             HttpServletRequest req, HttpServletResponse resp ) {

        Optional<User> userIfAny = assertUserValidity(token, osk);
        Cart cart;
        if (userIfAny.isPresent()) {
            cartService.removeItem(id, userIfAny.get());
            cart = cartService.getCart(userIfAny.get());
        }
        else {
            cartService.removeItem(id, osk);
            cart = cartService.getCart(osk);
        }

        String renderedCart = fromCheckout? renderCheckout(cart, req, resp)
                : renderCart(cart, req, resp);

        return new EffectiveCart(renderedCart, cart == null? 0 : cart.getSize());
    }

    /**
     * Создать заказ на основе текущего состояния корзины и сразу перейти к его оплате.
     * @return запрос на оплату заказа в виде html формы.
     */
    @PostMapping("/order")
    public InitHoldResponse createOrder(OrderCreationRequest request,
                                         @CookieValue(required = false) String osk,
                                         UserIdAuthenticationToken token) {

        Optional<User> userIfAny = assertUserValidity(token, osk);

        String comment = request.getComment();
        GuestOrderCreationParams params = new GuestOrderCreationParams()
                .setLoggedInUser(userIfAny.orElse(null))
                .setGuestToken(osk)

                .setNickname(request.getNickname())
                .setName(request.getName())
                .setPhone(request.getPhone())
                .setAddress(request.getAddress())
                .setCity(request.getCity())
                .setEmail(request.getEmail())
                .setZipCode(request.getZipCode())
                .setExtensiveAddress(request.getExtensiveAddress())
                .setComment(Strings.isNullOrEmpty(comment == null? null : comment.trim())?
                        null : comment.trim());

        Order order = cartService.createGuestOrder(params);
        OrderService.OrderToPayFor orderToPayFor = new OrderService.OrderToPayFor()
                .setId(order.getId())
                .setUnavailablePositions(Collections.emptyList())
                .setAvailablePositions(order.getOrderPositions().stream()
                        .map(OrderPosition::getId).collect(toList()));

        String paymentRequest = orderService.initHold(orderToPayFor, order.getBuyer());

        return new InitHoldResponse(renderHoldSubmitForm(paymentRequest));
    }

    @PatchMapping("/discount")
    public EffectiveCart updateDiscount(DiscountRequest request,
                                        @CookieValue(required = false) String osk,
                                        UserIdAuthenticationToken t,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws DiscountIsAlreadyUsedException, NotFoundException, OrderException {

        Optional<User> userIfAny = assertUserValidity(t, osk);
        if (userIfAny.isPresent()) {
            if (request.getCode() != null) {
                cartService.addDiscount(request.getCode(), userIfAny.get());
            }
            else {
                cartService.removeDiscount(userIfAny.get());
            }
        }
        else {
            if (request.getCode() != null) {
                cartService.addDiscount(request.getCode(), osk);
            }
            else {
                cartService.removeDiscount(osk);
            }
        }

        Cart nullableCart = userIfAny.isPresent()? cartService.getCart(userIfAny.get())
                : cartService.getCart(osk);

        String renderedCart = request.checkout? renderCheckout(nullableCart, req, resp)
                : renderCart(nullableCart, req, resp);

        return new EffectiveCart(renderedCart, nullableCart == null? 0 : nullableCart.getSize());
    }

    @PatchMapping("/items/{id}")
    public EffectiveCart changeSize(@PathVariable Long id, Long size,
                                    UserIdAuthenticationToken token,
                                    @CookieValue(required = false) String osk,
                                    HttpServletRequest req, HttpServletResponse resp ) {

        Optional<User> userIfAny = assertUserValidity(token, osk);
        Cart cart;
        if (userIfAny.isPresent()) {
            cartService.changeSize(id, size, userIfAny.get());
            cart = cartService.getCart(userIfAny.get());
        }
        else {
            cartService.changeSize(id, size, osk);
            cart = cartService.getCart(osk);
        }

        String renderedCart = renderCart(cart, req, resp);

        return new EffectiveCart(renderedCart, cart == null? 0 : cart.getSize());
    }

    @PatchMapping("/delivery")
    public EffectiveCart updateDelivery(DeliveryRequest request,
                                        @CookieValue(required = false) String osk,
                                        UserIdAuthenticationToken t,
                                        HttpServletRequest req,
                                        HttpServletResponse resp) {

        Optional<User> userIfAny = assertUserValidity(t, osk);
        cartService.updateDelivery(userIfAny.orElse(null), osk,
                request.getCity(),
                request.getAddress(),
                request.getZipCode(),
                request.getExtensiveAddress()
                );

        Cart nullableCart = userIfAny.isPresent()? cartService.getCart(userIfAny.get())
                : cartService.getCart(osk);

        String renderedCart = renderCheckout(nullableCart, req, resp);

        return new EffectiveCart(renderedCart, nullableCart == null? 0 : nullableCart.getSize());
    }

    private Optional<User> assertUserValidity(UserIdAuthenticationToken token, String osk) {

        if (osk == null && token == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

       /* запрос выполнил анонимный пользователь */
        if (token == null) {
            return Optional.empty();
        }

        User userIfAny = userRepository.findOne(token.getUserId());

        if (osk == null && userIfAny == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        return Optional.ofNullable(userIfAny);
    }

    private String renderCart(Cart cart, HttpServletRequest req, HttpServletResponse resp) {

        WebContext context = new WebContext(req, resp, ctx);
        context.setVariable("cart", cart);

        return templateEngine.process(
                "order/cart-content",
                new HashSet<>(Collections.singletonList("[th:fragment='content (cart)']")),
                context);
    }

    private String renderCheckout(Cart cart, HttpServletRequest req, HttpServletResponse resp) {

        WebContext context = new WebContext(req, resp, ctx);
        context.setVariable("cart", cart);

        return templateEngine.process(
                "order/checkout-content",
                new HashSet<>(Collections.singletonList("[th:fragment='content (cart)']")),
                context);
    }

    /** {@link OrderApi#renderHoldSubmitForm(String)}*/
    private String renderHoldSubmitForm(String paymentRequest) {

        Context context = new Context();
        context.setVariable("request", paymentRequest);
        context.setVariable("endpoint", acquirerWebEndpoint);

        return templateEngine.process("order/mdm_bank_hold_request_form", context);
    }

    @Getter @RequiredArgsConstructor
    private class EffectiveCart {

        private final String renderedContent;

        /** Число доступных товаров. */
        private final Integer size;
    }

    @Getter @Setter @Accessors(chain = true)
    private static class OrderCreationRequest {
        private String phone;
        private String name;
        private String nickname;
        private String email;
        private String city;
        private String address;
        private String extensiveAddress;
        private String zipCode;

        private String comment;
    }

    @Getter @Setter
    private static class DiscountRequest {
        private String code;
        private boolean checkout;
    }

    /** {@link OrderApi.InitHoldResponse}*/
    @RequiredArgsConstructor
    private class InitHoldResponse {
        public final String holdSubmitForm;
    }

    @Getter @Setter
    private static class DeliveryRequest {
        private String city;
        private String address;
        private String zipCode;

        private String extensiveAddress;
    }
}
