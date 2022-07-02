package su.reddot.domain.service.admin.user;

import su.reddot.domain.service.admin.user.moderation.view.AddModeratorResponseItem;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorAuthorityRequest;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorListView;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorView;
import su.reddot.domain.service.admin.user.userlist.view.UserListView;
import su.reddot.domain.service.admin.user.userpage.view.UserView;

import java.util.List;

public interface UserAdminService {

	/**
	 * Ищем всех пользователей в соответствии с указанными фильтрами
	 * и отдаем только часть из них, указанную в параметрах поиска
	 *
	 * @param spec набор фильтров, лимит и офсет
	 * @return список всех пользователей
	 */
	UserListView getUsers(UserAdminFilterSpec spec);

	UserView getUserInfo(Long userId);

	void setProStatus(Long userId, boolean proStatus) throws UserModificationException;

	long getAllUsersCount();

	/**
	 * Получаем:
	 * <ol>
	 * <li>Список всех прав в системе</li>
	 * <li>Список всех пользователей, у которых есть хоть одно право модератора, и список объектов (право:есть/нет)</li>
	 * </ol>
	 * Получаем список только модераторов
	 */
	ModeratorListView getModerators();

	void updateModeratorAuthorities(ModeratorAuthorityRequest request) throws ModeratorAuthorityException;

	List<AddModeratorResponseItem> findUsersByEmailPart(String emailPart);

	ModeratorView getModeratorCreationView(Long userId);
}
