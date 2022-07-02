package su.reddot.domain.service.admin.user.moderation.mapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.admin.UserAuthorityProjection;
import su.reddot.domain.model.Authority;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorAuthorityView;
import su.reddot.domain.service.admin.user.moderation.view.ModeratorView;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModeratorViewMapper {

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;

	/**
	 * @param userAuthorities список прав пользователя
	 * @param authorities     список всех модераторских прав
	 */
	public ModeratorView map(List<UserAuthorityProjection> userAuthorities, List<Authority> authorities) {
		UserAuthorityProjection firstProjection = userAuthorities.get(0);

		String firstName = firstProjection.getFirstName() != null ? firstProjection.getFirstName() : "%ИМЯ%";
		String lastName = firstProjection.getLastName() != null ? firstProjection.getLastName() : "%ФАМИЛИЯ%";

		List<ModeratorAuthorityView> moderatorAuthorityViews = authorities.stream()
				.map(authority -> new ModeratorAuthorityView(
						authority.getId(),
						authority.getName().name(),
						authority.getName().getDescription(),
						//проверяем, есть ли в списке прав пользователя текущее(в цикле) право
						userAuthorities.stream().filter(a -> a.getAuthorityName() == authority.getName())
								.findFirst().isPresent()
				)).collect(Collectors.toList());

		return new ModeratorView()
				.setId(firstProjection.getUserId())
				.setEmail(firstProjection.getEmail())
				.setNickname(firstProjection.getNickname())
				.setAvatar(firstProjection.getAvatarPath() != null ? urlPrefix + firstProjection.getAvatarPath() : null)
				.setAuthorities(moderatorAuthorityViews)
				.setName(firstName + " " + lastName);
	}
}
