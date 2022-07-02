package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

/**
 * @author Sergey Kultishev on 04.09/17
 */
@Controller
@RequestMapping("/profile/{userId}")
@Slf4j
@RequiredArgsConstructor
public class PublicProfileController {

    private final ProfileService      profileService;
    private final NotificationService notificationService;
    private final UserService         userService;
    private final FollowingService    followingService;

    @GetMapping
    public String getPublicProfile(@PathVariable Long userId, Model model, UserIdAuthenticationToken token) throws NotFoundException {
        if (! profileService.profileIsExist(userId)){
            throw new NotFoundException("404:  /profile/"+ userId.toString() + " are not found");
        }

        model.addAttribute("userProfile", profileService.getProfileView(userId));
        model.addAttribute("productsList", profileService.getCatalogProductPage(userId, ProductState.PUBLISHED, (token != null ? token.getUserId() : null)));
        return "profile/public-profile";
    }

    @GetMapping("/wishlist")
    public String getPublicWishList(@PathVariable Long userId, Model model, UserIdAuthenticationToken token){
        if (! profileService.profileIsExist(userId)){
            model.addAttribute("errorMessage","Пользователь отсутствует");
            return "oops";
        }

        model.addAttribute("userProfile", profileService.getProfileView(userId));
        model.addAttribute("productCards", profileService.getMyWishList(userId));
        return "profile/public-wishlist";

    }

    @GetMapping("/favorites")
    public String getPublicFavorites(@PathVariable Long userId, Model model, UserIdAuthenticationToken token){
        if (! profileService.profileIsExist(userId)){
            model.addAttribute("errorMessage","Пользователь отсутствует");
            return "oops";
        }

        model.addAttribute("userProfile", profileService.getProfileView(userId));
        model.addAttribute("productCards", profileService.getMyFavorites(userId));
        return "profile/public-favorites";

    }

    @GetMapping("/news")
    public String getPublicNews(@PathVariable Long userId, Model model, UserIdAuthenticationToken token)
    {
        if (!profileService.profileIsExist(userId)) {
            model.addAttribute("errorMessage","Пользователь отсутствует");
            return "oops";
        }
        model.addAttribute("userProfile", profileService.getProfileView(userId));
        model.addAttribute("actions", notificationService.findNewsByUserAndPresentAsView(userId, false, null));

        return "profile/public-news";
    }

    /** Список подписчиков. */
    @GetMapping("/followers")
    public String followers(@PathVariable Long userId, Model m, UserIdAuthenticationToken t) {

        User subject = userService.getUserById(userId).orElse(null);
        if (subject == null) {
            m.addAttribute("errorMessage","Пользователь отсутствует");
            return "oops";
        }

        User observer = t != null? userService.getUserById(t.getUserId())
                .orElseThrow(IllegalStateException::new)
                : null;

        FollowingService.GetBuilder followers = followingService.getFor(subject)
                .followers();

        if (observer != null) {
            followers.withFollowAvailability(observer);
        }

        m.addAttribute("followers", followers.build());
        m.addAttribute("userProfile", profileService.getProfileView(userId));
        m.addAttribute("observer", observer);

        return "profile/public-followers";
    }

    /** Список подписок. */
    @GetMapping("/followees")
    public String followees(@PathVariable Long userId, Model m, UserIdAuthenticationToken t) {

        /* Пользователь, профиль которого обозревается. */
        User subject = userService.getUserById(userId).orElse(null);
        if (subject == null) {
            m.addAttribute("errorMessage","Пользователь отсутствует");
            return "oops";
        }

        /* Пользователь, который вошел на сайт и просматривает публичный профиль. */
        User observer = t != null? userService.getUserById(t.getUserId())
                .orElseThrow(IllegalStateException::new)
                : null;

        FollowingService.GetBuilder followees = followingService.getFor(subject)
                .followees();

        if (observer != null) {
            followees.withFollowAvailability(observer);
        }

        m.addAttribute("followees", followees.build());
        m.addAttribute("userProfile", profileService.getProfileView(userId));
        m.addAttribute("observer", observer);

        return "profile/public-followees";
    }

    @ModelAttribute
    public void populateWithFollowRelated(@PathVariable  Long userId,
                                          UserIdAuthenticationToken token,
                                          Model m) {

        User subject = userService.getUserById(userId).orElse(null);
        if (subject == null) return;

        m.addAttribute("followersCount",
                followingService.getFor(subject)
                        .followers().count());

        m.addAttribute("followeesCount",
                followingService.getFor(subject)
                        .followees().count());

        m.addAttribute("followed", profileService.readingUserFollowProfile(
                userId,
                token != null ? token.getUserId() : null));

    }
}
