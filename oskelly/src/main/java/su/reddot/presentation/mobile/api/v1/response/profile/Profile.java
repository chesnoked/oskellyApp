package su.reddot.presentation.mobile.api.v1.response.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 09.11.17.
 */
@RequiredArgsConstructor
@Getter
public class Profile {

	private final Long id;
	private final String city;
	private final String nickname;
	private final String avatar;
	private final Integer followersCount;
	private final Integer followingsCount;
	private final boolean pro;
	private final Integer likesCount;
	private final Integer pointsCount;
	private final boolean doIFollow;
	private final boolean trusted;
	private final List<Following> followers;
	private final List<Following> followings;
	private final boolean isMyProfile;
	private final Integer productsForSale;
}
