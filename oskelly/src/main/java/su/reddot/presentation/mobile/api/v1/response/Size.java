package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
public class Size {
	private final String type;
	private final List<Value> values;

	@Getter
	@RequiredArgsConstructor
	public static class Value {
		private final Long id;
		private final String value;
		private final BigDecimal lowestPrice;
	}
}
