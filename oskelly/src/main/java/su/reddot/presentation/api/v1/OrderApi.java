package su.reddot.presentation.api.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.Discount;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class OrderApi {

    private final OrderService orderService;
    private final UserService userService;

    private final SpringTemplateEngine templateEngine;

    // FIXME форму должен отдавать платежный шлюз
    @Value("${acquirer.mdm-pay.web-endpoint}")
    private String acquirerWebEndpoint;

    @PostMapping("/{id}/hold")
    InitHoldResponse checkOrderPositionsAvailabilityAndInitHold(
            @PathVariable Long id,
            /* Передавать доступные и недоступные товары нужно только для нового заказа,
             * для повторной же оплаты эти данные передавать необязательно. */
            @RequestBody(required = false) OrderPositionsContainer c,
            UserIdAuthenticationToken token) {

        User   user           = userService.getUserById(token.getUserId()).get();
        String paymentRequest = orderService.initHold(
                new OrderService.OrderToPayFor()
                        .setId(id)
                        .setAvailablePositions(c != null? c.getAvailablePositions() : null)
                        .setUnavailablePositions(c != null? c.getUnavailablePositions() : null),
                user);

        return new InitHoldResponse(renderHoldSubmitForm(paymentRequest));
    }

    @PutMapping("/{id}/delivery")
    ResponseEntity<?> setDeliveryRequisite(@PathVariable Long id, @RequestBody DeliveryRequisite r,
                                           @RequestParam(defaultValue = "false") boolean updateProfile,
                                           UserIdAuthenticationToken t) {

        orderService.setDeliveryRequisite(id, r, userService.getUserById(t.getUserId()).get());

        if (updateProfile) { userService.setDeliveryRequisite(t.getUserId(), r); }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/discount")
    public Discount addDiscount(@PathVariable Long id, @RequestBody DiscountRequest discountRequest, UserIdAuthenticationToken t)
            throws OrderException, NotFoundException, DiscountIsAlreadyUsedException {

        return orderService.addDiscount(id, userService.getUserById(t.getUserId()).get(), discountRequest.getCode());

    }

    @DeleteMapping("/{id}/discount")
    public ResponseEntity<?> removeDiscount(@PathVariable Long id, UserIdAuthenticationToken t)
            throws OrderException, NotFoundException {

        orderService.removeDiscount(id, userService.getUserById(t.getUserId()).get());
        return ResponseEntity.ok().build();
    }

    private String renderHoldSubmitForm(String paymentRequest) {

        Context context = new Context();
        context.setVariable("request", paymentRequest);
        context.setVariable("endpoint", acquirerWebEndpoint);

        return templateEngine.process("order/mdm_bank_hold_request_form", context);
    }

    @RequiredArgsConstructor
    private class InitHoldResponse {
        public final String holdSubmitForm;
    }

    @Getter @Setter
    private static class DiscountRequest {
        private String code;
    }

    @Getter @Setter
    private static class OrderPositionsContainer {

        List<Long> availablePositions;

        List<Long> unavailablePositions;
    }
}
