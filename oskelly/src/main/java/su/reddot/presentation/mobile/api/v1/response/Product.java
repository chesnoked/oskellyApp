package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 03.10.17.
 */
@Getter
@RequiredArgsConstructor
public class Product {
	private final Long id;
	private final String price;
	private final String category;
	private final String brand;
	private final String image;
	private final Size size;
	private final Integer likesCount;
	private final boolean doILike;
	private final boolean isNotUsedYet;
	private final boolean hasDiscount;

	/**
	 * Рекомендованная цена и экономия
	 * @apiNote nullable
	 * */
	private final String rrp;
	private final Integer savingsValue;
}
