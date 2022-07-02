package su.reddot.domain.service.admin.user.moderation.view;

import lombok.Value;

@Value
public class ModeratorAuthorityView {

	/**
	 * Id права в системе
	 */
	private Long id;

	/**
	 * Наименование права в таблице прав
	 */
	private String name;

	/**
	 * Описание данного права
	 */
	private String description;

	/**
	 * Есть ли это право в текущего модератора
	 */
	private boolean checked;
}
