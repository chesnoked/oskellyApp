package su.reddot.presentation.mobile.api.v1.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 19.10.17.
 */
@RequiredArgsConstructor
@Getter
public class SelectedAttributeValue {
	private final Long id;
	private final String name;
}
