package su.reddot.presentation.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

@RestController
@RequestMapping("/api/v1/profiles")
@PreAuthorize("isAuthenticated()")
@Slf4j
@RequiredArgsConstructor
public class ProfileApi {

    private final FollowingService followingService;
    private final ProfileService   profileService;
    private final UserService      userService;

    @PostMapping("/self/followees")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> follow(@RequestParam("userId") Long userId, UserIdAuthenticationToken token){

        User follower = userService.getUserById(token.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с идентификатором " + token.getUserId() + "не найден"));

        User following = userService.getUserById(userId).orElseThrow(
                () -> new IllegalArgumentException("Пользователь, на которого оформляется подписка, не найден"));

        followingService.follow(follower, following);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/self/followees/{userId}")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> stopFollowing(@PathVariable("userId") Long userId, UserIdAuthenticationToken token){

        User follower = userService.getUserById(token.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с идентификатором " + token.getUserId() + "не найден"));

        User following = userService.getUserById(userId).orElseThrow(
                () -> new IllegalArgumentException("Пользователь, на которого оформляется подписка, не найден"));

        followingService.unfollow(follower, following);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/self/price-tracking/{productId}")
    public ResponseEntity<?> removePriceUpdateSub(@PathVariable Long productId, User u) {

        profileService.dropPriceTracking(productId, u);

        return ResponseEntity.ok().build();
    }

    @ModelAttribute
    public User loggedInUserPlease(UserIdAuthenticationToken t) {
        return userService.getUserById(t.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с идентификатором " + t.getUserId() + "не найден"));
    }

}
