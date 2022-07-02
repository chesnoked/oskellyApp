package su.reddot.presentation.mobile.api.v1.response.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 09.11.17.
 */
@RequiredArgsConstructor
@Getter
public class Following {
	private final Long id;
	private final String nickname;
	private final boolean doIFollow;
	private final boolean pro;
	private final String avatar;
	private final Integer productsForSale;
	private final Integer subscribers;
}
