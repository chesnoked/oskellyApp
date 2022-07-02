package su.reddot.presentation.api.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.discount.impl.DefaultDiscountService;
import su.reddot.domain.service.discount.validation.Amount;
import su.reddot.domain.service.discount.validation.Recipient;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/discount")
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class DiscountApi {

    private final DefaultDiscountService discountService;
    private final UserService            userService;

    private final TemplateEngine templateEngine;

    // FIXME форму должен отдавать платежный шлюз
    @Value("${acquirer.mdm-pay.web-endpoint}")
    private String acquirerWebEndpoint;

    @PostMapping("/gift-cards")
    public ResponseEntity<?> create(@Validated @RequestBody GiftCardRequest request, UserIdAuthenticationToken t) {

        User buyer = userService.getUserById(t.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь не найден"));

        GiftCard card = discountService.createGiftCard(
                new BigDecimal(request.getAmount()),
                request.getGivingName(), request.getRecipient(), buyer);

        String cardLocation
                = ServletUriComponentsBuilder.fromCurrentContextPath().path("/gift-cards/{id}")
                .buildAndExpand(card.getId()).toUriString();

        return ResponseEntity.created(URI.create(cardLocation)).body(new GiftCardCreateResponse(card.getId()));
    }

    @GetMapping("/gift-cards/{id}/payment-request")
    public String getPaymentRequest(@PathVariable Long id, UserIdAuthenticationToken t) {

        User buyer = userService.getUserById(t.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь не найден"));

        String paymentRequest = discountService.getPaymentRequest(id, buyer);

        return renderPaymentForm(paymentRequest);
    }

    private String renderPaymentForm(String paymentRequest) {

        Context context = new Context();
        context.setVariable("request", paymentRequest);
        context.setVariable("endpoint", acquirerWebEndpoint);

        return templateEngine.process("order/mdm_bank_payment_request_form", context);
    }

    @Getter @Setter
    private static class GiftCardRequest {

        @Amount
        private String amount;

        @NotBlank(message = "Имя не может быть пустым")
        private String givingName;

        @Recipient
        private su.reddot.domain.model.discount.GiftCard.Recipient recipient;
    }

    @Getter @Setter @RequiredArgsConstructor
    private static class GiftCardCreateResponse {
        private final Long id;
    }
}
