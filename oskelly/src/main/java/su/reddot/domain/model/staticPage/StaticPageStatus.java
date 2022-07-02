package su.reddot.domain.model.staticPage;

import lombok.Getter;

/**
 * @author Vitaliy Khludeev on 02.08.17.
 */
@Getter
public enum StaticPageStatus {

	DRAFT("Черновик"),
	PUBLISHED("Опубликован");

	private String description;

	StaticPageStatus(String description) {
		this.description = description;
	}
}
