package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.presentation.view.product.ProductView;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@RequiredArgsConstructor
@Getter
public class ProductResponse {

	private final Long id;
	private final Long sellerId;
	private final String seller;
	private final String registrationDate;
	private final String city;
	private final Long brandId;
	private final String brand;
	private final String category;
	private final Long categoryId;
	private final Size size;
	private final BigDecimal rrp;
	private final String condition;
	private final String description;
	private final List<Attribute> attributes;
	private final Boolean ourChoice;
	private final List<String> smallImages;
	private final List<String> largeImages;
	private final String avatar;
	private final boolean pro;
	private final Integer soldProductsCount;
	private final Integer publishedProductsCount;
	private final Integer likesCount;
	private final boolean doIFollow;
	private final boolean inMyWishList;
	private final boolean doIWatchOutForPrice;
	private final boolean isAvailable;
	private final boolean doILike;
	private final ProductView.OfferRelated offerRelated;
	private final String price;
	private final String startPrice;
	private final String currentPrice;
	private final boolean hasDiscount;
	private final boolean canBeAddedToCart;
	private final String reasonWhyItCannotBeAddedToCart;


	@RequiredArgsConstructor
	@Getter
	public static class Attribute {
		private final String name;
		private final String value;
	}
}
