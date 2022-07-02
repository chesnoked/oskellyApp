package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.like.LikeService;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.notification.NotificationView;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.mobile.api.v1.response.profile.Following;
import su.reddot.presentation.mobile.api.v1.response.profile.Profile;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Контроллер для публичного профиля
 * Не путать с личным кабинетом
 * @see AccountRestControllerV1
 * @author Vitaliy Khludeev on 04.09.17.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mobile/api/v1/profile")
@PreAuthorize("isFullyAuthenticated()")
public class ProfileRestControllerV1 {

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;
	private final UserService userService;
	private final FollowingService followingService;
	private final LikeService likeService;
	private final NotificationService notificationService;
	private final ProfileService profileService;

	@GetMapping
	public ResponseEntity<Profile> getMyProfile(UserIdAuthenticationToken t) {
		return getPublicProfileResponseEntity(t.getUserId(), t);
	}

	@GetMapping("/{profileId}")
	public ResponseEntity<Profile> getProfile(@PathVariable Long profileId, UserIdAuthenticationToken t) {
		return getPublicProfileResponseEntity(profileId, t);
	}

	@PostMapping("/subscriptions")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<?> startFollowing(@RequestParam("userId") Long userId, UserIdAuthenticationToken token){

		User follower = userService.getUserById(token.getUserId()).orElseThrow(
				() -> new IllegalArgumentException("Пользователь с идентификатором " + token.getUserId() + "не найден"));

		User following = userService.getUserById(userId).orElseThrow(
				() -> new IllegalArgumentException("Пользователь, на которого оформляется подписка, не найден"));

		followingService.follow(follower, following);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/subscriptions")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<?> stopFollowing(@RequestParam("userId") Long userId, UserIdAuthenticationToken token){

		User follower = userService.getUserById(token.getUserId()).orElseThrow(
				() -> new IllegalArgumentException("Пользователь с идентификатором " + token.getUserId() + "не найден"));

		User following = userService.getUserById(userId).orElseThrow(
				() -> new IllegalArgumentException("Пользователь, на которого оформляется подписка, не найден"));

		followingService.unfollow(follower, following);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/following/toggle")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<?> toggleFollowing(@RequestParam("userId") Long userId, UserIdAuthenticationToken token){

		User follower = userService.getUserById(token.getUserId()).orElseThrow(
				() -> new IllegalArgumentException("Пользователь с идентификатором " + token.getUserId() + "не найден"));

		User following = userService.getUserById(userId).orElseThrow(
				() -> new IllegalArgumentException("Пользователь, на которого оформляется подписка, не найден"));

		followingService.toggle(follower, following);

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/news/{profileId}")
	public ResponseEntity<List<NotificationView>> getNotifications(@PathVariable Long profileId, TimeZone timezone) {
		return ResponseEntity.ok(notificationService.findNewsByUserAndPresentAsView(profileId, false, timezone));
	}

	private ResponseEntity<Profile> getPublicProfileResponseEntity(@PathVariable Long profileId, UserIdAuthenticationToken t) {
		User u = userService.getUserById(profileId).orElseThrow(() -> new IllegalArgumentException("Профиль не существует"));
		User me = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		ProfileView profileView = profileService.getProfileView(u.getId());
		Profile r = new Profile(
				u.getId(),
				u.getSellerRequisite() != null? u.getSellerRequisite().getCity() : null,
				u.getNickname(),
				u.getAvatarPath() != null ? urlPrefix + u.getAvatarPath() : User.noImageUrl,
				followingService.getFollowersCount(u),
				followingService.getFollowingsCount(u),
				u.isPro(),
				likeService.countByAllUserLikeables(u, su.reddot.domain.model.product.Product.class),
				0,
				followingService.isFollowingExists(me, u),
				false,
				followingService.getFollowers(u).stream().map(f -> of(me, f)).collect(Collectors.toList()),
				followingService.getFollowings(u).stream().map(f -> of(me, f)).collect(Collectors.toList()),
				profileId.equals(t.getUserId()),
				profileView.getPublishedProductsCount()

		);
		return ResponseEntity.ok(r);
	}

	private Following of(User me, User f) {
		ProfileView profileView = profileService.getProfileView(f.getId());
		return new Following(
				f.getId(),
				f.getNickname(),
				followingService.isFollowingExists(me, f), // FIXME: много запросов в БД
				f.isPro(),
				Optional.ofNullable(f.getAvatarPath()).map(s -> urlPrefix + s).orElse(User.noImageUrl),
				profileView.getPublishedProductsCount(),
				profileView.getSubscribers()
		);
	}
}
