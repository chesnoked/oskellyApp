package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 06.09.17.
 */
@RequiredArgsConstructor
@Getter
public class AttributeValue {

	private final Long id;

	private final Long attributeId;

	private final String name;

	private final String value;
}
