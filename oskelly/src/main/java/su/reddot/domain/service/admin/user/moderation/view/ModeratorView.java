package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ModeratorView {

	private Long id;

	/**
	 * ФИО пользователя
	 */
	private String name;
	private String email;
	private String nickname;

	/**
	 * Ссылка на аватарку пользователя. Если ее нет, то null.
	 */
	private String avatar;

	/**
	 * Список соответствий (право: есть/нет у данного пользователя)
	 */
	private List<ModeratorAuthorityView> authorities;

}
