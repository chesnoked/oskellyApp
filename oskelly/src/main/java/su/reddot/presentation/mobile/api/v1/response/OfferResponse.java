package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 08.02.18.
 */
@RequiredArgsConstructor
@Getter
public class OfferResponse {
	private final Long productId;
	private final String brand;
	private final String productName;
	private final String size;
	private final String image;
	private final boolean canBeAddedToCart;
	private final boolean waitingForConfirmation;
	private final Long waitingForConfirmationOfferId;
	private final List<OfferHistory> history;
	private final String optionalFailMessage;

	@RequiredArgsConstructor
	@Getter
	public static class OfferHistory {
		private final String text;
		private final String price;
		private final boolean bold;
	}
}
