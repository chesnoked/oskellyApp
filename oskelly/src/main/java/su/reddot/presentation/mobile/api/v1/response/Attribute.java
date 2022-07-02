package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 06.09.17.
 */
@RequiredArgsConstructor
@Getter
public class Attribute {

	private final Long id;

	private final String name;

	private final List<AttributeValue> values;
}