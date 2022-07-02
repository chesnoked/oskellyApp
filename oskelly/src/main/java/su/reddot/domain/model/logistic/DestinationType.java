package su.reddot.domain.model.logistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 27.08.17.
 */
@RequiredArgsConstructor @Getter
public enum DestinationType {

	OFFICE("Офис"),
	SELLER("Продавец"),
	BUYER("Покупатель");

	private final String description;

}
