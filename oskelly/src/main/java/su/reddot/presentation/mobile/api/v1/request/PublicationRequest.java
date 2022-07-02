package su.reddot.presentation.mobile.api.v1.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.size.SizeType;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @author Vitaliy Khludeev on 19.10.17.
 */
@RequiredArgsConstructor
@Getter
public class PublicationRequest {
	private final long id;
	private final SizeType selectedSizeType;
	private final Long selectedSizeId;
	private final String description;
	private final boolean vintage;
	private final String model;
	private final String origin;
	private final BigDecimal purchasePrice;
	private final Integer purchaseYear;
	private final String serialNumber;
	private final Long selectedCondition;
	private final BigDecimal priceWithCommission;
	private final BigDecimal priceWithoutCommission;
	private final Collection<SelectedAttributeValue> selectedAttributeValues;
	private final SellerRequisite sellerRequisite;
	private final boolean isCompletePublication;
	private final Long brandId;
	private final Long categoryId;
}
