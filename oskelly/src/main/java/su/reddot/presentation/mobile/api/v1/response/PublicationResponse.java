package su.reddot.presentation.mobile.api.v1.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.category.PublicationPhotoSample;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.size.CatalogSize;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 06.09.17.
 */
@RequiredArgsConstructor
@Getter
public class PublicationResponse {

	private final Long id;
	private final List<Long> selectedCategories;
	private final Long category;
	private final String categoryName;
	private final Long brand;
	private final String brandName;
	private final List<AttributeValue> selectedAttributeValues;
	private final SizeType selectedSizeType;
	private final SizeValue selectedSize;
	private final List<Image> images;
	private final String description;
	private final boolean vintage;
	private final String model;
	private final String origin;
	private final BigDecimal purchasePrice;
	private final Integer purchaseYear;
	private final String serialNumber;
	private final Long selectedCondition;
	private final BigDecimal commission;
	private final BigDecimal priceWithCommission;
	private final BigDecimal priceWithoutCommission;
	private final SellerRequisite sellerRequisite;
	private final Boolean isCompletePublication;
	private final List<PublicationPhotoSample> samples;
	private final List<Integer> completedSteps;

	@RequiredArgsConstructor
	@Getter
	public static class Image {

		private final String url;

		private final Integer order;
	}

	@RequiredArgsConstructor
	@Getter
	public static class SizeValue {
		private final Long id;
		private final String value;
	}
}
