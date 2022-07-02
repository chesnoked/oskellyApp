package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.admin.user.ModeratorAuthorityException;
import su.reddot.domain.service.admin.user.UserAdminFilterSpec;
import su.reddot.domain.service.admin.user.UserAdminService;
import su.reddot.domain.service.admin.user.UserModificationException;
import su.reddot.domain.service.admin.user.moderation.view.AddModeratorResponseItem;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorAuthorityRequest;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorListView;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorView;
import su.reddot.domain.service.admin.user.userlist.view.UserListView;
import su.reddot.domain.service.admin.user.userpage.view.UserView;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

import static su.reddot.presentation.Utils.badResponseWithFieldError;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasAnyAuthority(T(su.reddot.domain.model.enums.AuthorityName).USER_MODERATION,"
							+ "T(su.reddot.domain.model.enums.AuthorityName).AUTHORITY_MODERATION)")
@Slf4j
public class UserAdminController {

	private static final int DEFAULT_LIMIT = 50;

	private final UserAdminService userAdminService;

	@GetMapping
	public String getUserPage(Model model) {

		UserListView users = userAdminService.getUsers(
				new UserAdminFilterSpec(false, false, false, 1, DEFAULT_LIMIT)
		);
		long allUsersCount = userAdminService.getAllUsersCount();
		model.addAttribute("users", users);
		model.addAttribute("allUsersCount", allUsersCount);

		return "admin/content-management/users/users";
	}

	@GetMapping("/list")
	public String getUserList(@RequestParam(value = "isPro", required = false) boolean isPro,
	                          @RequestParam(value = "isNew", required = false) boolean isNew,
	                          @RequestParam(value = "isSeller", required = false) boolean isSeller,
	                          @RequestParam int page, Model model) {

		UserListView users = userAdminService.getUsers(
				new UserAdminFilterSpec(isPro, isSeller, isNew, page, DEFAULT_LIMIT)
		);
		model.addAttribute("users", users);

		return "admin/content-management/users/userlist";

	}

	@GetMapping("/{userId}")
	public String getUserInfo(@PathVariable Long userId, Model model, UserIdAuthenticationToken token) {

		UserView userInfo = userAdminService.getUserInfo(userId);
		model.addAttribute("user", userInfo);

		return "admin/content-management/users/user";
	}

	@GetMapping("/moderators")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).AUTHORITY_MODERATION)")
	public String getAdminsPage(Model model) {

		ModeratorListView moderators = userAdminService.getModerators();

		model.addAttribute("moderators", moderators);

		/* так как на страницу управления правами могут перейти только пользователи,
		которые имеют полномочие управления правами,
		то часть интерфейса, отвечающего за настройку прав будет для них всегда активна */
		model.addAttribute("canEditPermissions", true);

		return "admin/content-management/users/moderators";
	}

	@GetMapping("/moderators/find_users")
	public String findUsersForModeration(@RequestParam String email, Model model) {

		List<AddModeratorResponseItem> usersByEmailPart = userAdminService.findUsersByEmailPart(email);
		model.addAttribute("users", usersByEmailPart);

		return "admin/content-management/users/moderator-add-view";
	}

	@GetMapping("/moderators/create_view")
	public String getModeratorCreateView(@RequestParam Long userId, Model model) {

		ModeratorView moderatorCreationView = userAdminService.getModeratorCreationView(userId);
		model.addAttribute("moderator", moderatorCreationView);
		return "admin/content-management/users/moderator-view :: moderator-reveal";
	}

	/* FIXME апи должно находится в отдельном, соответствующем ему пакете, не в классе веб контроллера */
	@PutMapping("/set_pro/{userId}")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).AUTHORITY_MODERATION)")
	public ResponseEntity<?> setUserProStatus(@PathVariable Long userId, @RequestParam Boolean proStatus) {

		if (proStatus == null) {
			return badResponseWithFieldError("Ошибка", "Некорректный параметр PRO статуса");
		}
		try {
			userAdminService.setProStatus(userId, proStatus);
			return ResponseEntity.ok().build();
		} catch (UserModificationException e) {
			return badResponseWithFieldError("Ошибка", e.getLocalizedMessage());
		}
	}

	@PutMapping("/moderators/authorities")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).AUTHORITY_MODERATION)")
	public ResponseEntity<?> updateAuthorities(@RequestBody ModeratorAuthorityRequest request,
											   UserIdAuthenticationToken token) {

		if (request.getUserId().equals(token.getUserId())) {
			return badResponseWithFieldError("Ошибка", "Нельзя менять свои собственные права доступа");
		}

		try {
			userAdminService.updateModeratorAuthorities(request);
		} catch (ModeratorAuthorityException e) {
			log.error(e.getLocalizedMessage(), e);
			return badResponseWithFieldError("Ошибка", e.getLocalizedMessage());
		}

		return ResponseEntity.ok().build();
	}
}
