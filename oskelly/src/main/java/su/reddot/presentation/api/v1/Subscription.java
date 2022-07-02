package su.reddot.presentation.api.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.subscription.SubscriptionService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class Subscription {

    private final SubscriptionService subscriptionService;
    private final UserService         userService;

    /** Создать оповещение о появлении новых товаров на сайте. */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SubscriptionRequest r, User u) {
        subscriptionService.createAlertSubscription(u,
                r.getBrand(), r.getSize(), r.getCategory(),
                r.getSizeType(), r.getCondition(), r.getAttributes());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> remove(@PathVariable Long id, UserIdAuthenticationToken t) {
        User user = userService.getUserById(t.getUserId())
                .orElseThrow(IllegalArgumentException::new);

        subscriptionService.removeSubscription(id, user);

        return ResponseEntity.ok().build();
    }

    @ModelAttribute
    User user(UserIdAuthenticationToken t) {
        return userService.getUserById(t.getUserId()).orElse(null);
    }

    @Getter @Setter
    private static class SubscriptionRequest {
        private Long       category;
        private Long       brand;
        private Long       condition;
        private SizeType   sizeType;
        private Long       size;
        private List<Long> attributes;
    }
}

