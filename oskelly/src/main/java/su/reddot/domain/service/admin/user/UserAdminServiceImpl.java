package su.reddot.domain.service.admin.user;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.AuthorityRepository;
import su.reddot.domain.dao.UserAuthorityBindingRepository;
import su.reddot.domain.dao.admin.*;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.user.User;
import su.reddot.domain.model.user.UserAuthorityBinding;
import su.reddot.domain.service.admin.user.moderation.mapper.ModeratorViewMapper;
import su.reddot.domain.service.admin.user.moderation.view.*;
import su.reddot.domain.service.admin.user.userlist.mapper.UserListItemViewMapper;
import su.reddot.domain.service.admin.user.userlist.view.UserListItemView;
import su.reddot.domain.service.admin.user.userlist.view.UserListView;
import su.reddot.domain.service.admin.user.userpage.mapper.UserViewMapper;
import su.reddot.domain.service.admin.user.userpage.view.UserView;
import su.reddot.infrastructure.security.persistentsession.PersistentSessionService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

	private final UserAdminRepository userAdminRepository;
	private final AuthorityRepository authorityRepository;
	private final UserAuthorityBindingRepository userAuthorityBindingRepository;

	private final UserViewMapper userViewMapper;
	private final ModeratorViewMapper moderatorViewMapper;
	private final UserListItemViewMapper userListItemViewMapper;

	private final PersistentSessionService persistentSessionService;

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;

	@Override
	public UserListView getUsers(UserAdminFilterSpec spec) {

		List<UserProductsAndOrdersProjection> projections =
				userAdminRepository.getUserStats(
						spec.isPro(),
						spec.isSeller(),
						spec.isNew(),
						(spec.getPage() - 1) * spec.getLimit(),
						spec.getLimit()
				);

		/*
			у нас на каждого пользователя может приходиться по несколько строк (зависит от списков товаров).
			Группируем по пользователю
			Слева у нас пользователь, справа данные по нему (отличия: состояние товара и количество товаров с таким состоянием)
		 */
		Map<BigInteger, List<UserProductsAndOrdersProjection>> projectionsGroupedByUser =
				projections.stream().collect(Collectors.groupingBy(UserProductsAndOrdersProjection::getUserid));

		//после группировки у нас слетает порядок пользователей. Сортируем группы
		projectionsGroupedByUser = new TreeMap<>(projectionsGroupedByUser);

		List<UserListItemView> userListItemViews = new ArrayList<>();
		for (Map.Entry<BigInteger, List<UserProductsAndOrdersProjection>> entry : projectionsGroupedByUser.entrySet()) {
			UserListItemView userListItemView = userListItemViewMapper.mapUserProjection(entry.getValue());
			userListItemViews.add(userListItemView);
		}
		return new UserListView(userListItemViews, spec.getPage(), userListItemViews.size());
	}

	@Override
	public UserView getUserInfo(Long userId) {
		UserInfoProjection infoProjection = userAdminRepository.getUserInfoById(userId);
		return userViewMapper.mapUserProjection(infoProjection);
	}

	@Override
	public void setProStatus(Long userId, boolean proStatus) throws UserModificationException {
		User user = userAdminRepository.findOne(userId);
		if (user == null) {
			throw new UserModificationException("Пользователь не найден в системе");
		}

		if (!proStatus) {
			throw new UserModificationException("Нельзя отключить PRO статус пользователя!");
		}

		user.setProStatusTime(LocalDateTime.now());
		userAdminRepository.save(user);
	}

	@Override
	public long getAllUsersCount() {
		return userAdminRepository.count();
	}

	@Override
	public ModeratorListView getModerators() {
		List<Authority> authorities = authorityRepository.findByType(Authority.AuthorityType.MODERATOR);

		List<UserAuthorityProjection> allUsersWithAuthorities = userAdminRepository.getAllModeratorsWithTheirAuthorities();

		//группируем по пользователю (в каждой записи в хештаблице у нас слева пользователь, справа пачка его прав)
		Map<Long, List<UserAuthorityProjection>> users =
				allUsersWithAuthorities.stream().collect(Collectors.groupingBy(UserAuthorityProjection::getUserId));

		List<ModeratorView> moderatorViews = new ArrayList<>();
		for (Map.Entry<Long, List<UserAuthorityProjection>> entry : users.entrySet()) {
			moderatorViews.add(moderatorViewMapper.map(entry.getValue(), authorities));
		}
		return new ModeratorListView(
				authorities.stream().map(a -> a.getName().getDescription()).collect(toList()),
				moderatorViews
		);
	}

	@Override
	@Transactional
	public void updateModeratorAuthorities(ModeratorAuthorityRequest request) throws ModeratorAuthorityException {
		List<Authority> moderatorAuthorities = authorityRepository.findByType(Authority.AuthorityType.MODERATOR);
		List<AuthorityRequestItem> authorities = request.getAuthorities();

		/*
			В этом блоке мы проверяем, что все права, которые мы передали в запросе, являются правами модератора
			Мало ли, вдруг мы через js передадим запрос на установление права администратора всея системы
		 */
		for (AuthorityRequestItem authorityRequestItem : authorities) {
			Optional<Authority> authorityMatches = moderatorAuthorities.stream().filter(moderatorAuthority ->
					moderatorAuthority.getId().equals(authorityRequestItem.getAuthorityId())
			).findFirst();
			if (!authorityMatches.isPresent()) {
				throw new ModeratorAuthorityException("Некорректное право доступа");
			}
		}

		User user = userAdminRepository.findOne(request.getUserId());

		for (AuthorityRequestItem item : authorities) {
			if (item.isChecked()) {
				addUserAuthority(user, item.getAuthorityId(), moderatorAuthorities);
			} else {
				deleteUserAuthority(user, item.getAuthorityId());
			}
		}

		persistentSessionService.updateUserSecurityContext(user.getId());
	}

	@Override
	public List<AddModeratorResponseItem> findUsersByEmailPart(String emailPart) {
		String likeExpr = "%" + emailPart + "%";
		List<UserEmailProjection> allSimpleUsersByEmailPart = userAdminRepository.findTop15SimpleUsersByEmailPart(likeExpr);
		return allSimpleUsersByEmailPart.stream().map(user -> {
					String firstName = user.getFirstName() != null ? user.getFirstName() : "%ИМЯ%";
					String lastName = user.getLastName() != null ? user.getLastName() : "%ФАМИЛИЯ%";
					return new AddModeratorResponseItem(
							user.getId(),
							firstName + " " + lastName,
							user.getEmail()
					);
				}
		).collect(toList());
	}

	@Override
	public ModeratorView getModeratorCreationView(Long userId) {
		List<Authority> moderatorAuthorities = authorityRepository.findByType(Authority.AuthorityType.MODERATOR);
		UserInfoProjection userInfo = userAdminRepository.getUserInfoById(userId);
		ModeratorView moderatorView = new ModeratorView();
		String firstName = userInfo.getFirstName() != null ? userInfo.getFirstName() : "%ИМЯ%";
		String lastName = userInfo.getLastName() != null ? userInfo.getLastName() : "%ФАМИЛИЯ%";
		moderatorView.setName(firstName + " " + lastName);
		moderatorView.setAvatar(userInfo.getAvatarPath() != null ? urlPrefix + userInfo.getAvatarPath() : null);
		moderatorView.setEmail(userInfo.getEmail());
		moderatorView.setId(userInfo.getId());
		moderatorView.setNickname(userInfo.getNickname());
		moderatorView.setAuthorities(moderatorAuthorities.stream().map(
				a -> new ModeratorAuthorityView(a.getId(), a.getName().name(), a.getName().getDescription(), false))
				.collect(toList()));
		return moderatorView;
	}

	/**
	 * Добавляем право пользователю, если у этого пользователя еще нет такого права
	 *
	 * @param user                 пользователь, которому мы ходим добавить право
	 * @param authorityId          id права, которое нам нужно добавить
	 * @param avalaibleAuthorities те права, из числа которых мы можем добавлять права
	 */
	private void addUserAuthority(User user, Long authorityId, List<Authority> avalaibleAuthorities) {
		boolean userAlreadyHasAuthority = user.getUserAuthorityBindings().stream()
				.filter(binding -> binding.getAuthority().getId().equals(authorityId))
				.findFirst().isPresent();
		if (!userAlreadyHasAuthority) {
			avalaibleAuthorities.stream().filter(a -> a.getId().equals(authorityId))
					.findFirst().ifPresent(a -> {
				userAuthorityBindingRepository.save(new UserAuthorityBinding(user, a));
			});
		}
	}

	/**
	 * Удаляем право пользователю
	 *
	 * @param user        пользователь
	 * @param authorityId id права, которое нам нужно убрать
	 */
	private void deleteUserAuthority(User user, Long authorityId) {
		user.getUserAuthorityBindings().removeIf(
				binding -> binding.getAuthority().getId().equals(authorityId)
		);
	}

}
