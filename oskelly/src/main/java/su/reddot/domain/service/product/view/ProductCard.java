package su.reddot.domain.service.product.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.ProductState;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Карточка товара, содержит краткую информацию о нем
 * @see su.reddot.presentation.mobile.api.v1.PublicationRestControllerV1
 */
@Getter @Setter @Accessors(chain = true)
public class ProductCard {

	private Long id;

	/** Актуальная ссылка на товар.
	 * В зависимости от состояния товара можно перейти на страницу товара или
	 * на страницу редактирвания товара в личном кабинете
	 **/
	private String effectiveUrl;

	/** Наименование товара */
	private String name;

	private String brand;

	private String category;

	private String singularCategoryName;

	/** Ссылка на миниатюру основного изображения товара */
	private String primaryImageUrl;

	private ProductState state;

	/** Имеющиеся в продаже размеры*/
	private SizeSummary sizeSummary;

	/** Минимальная цена товара (может быть разной в рамках одного товара) */
	private BigDecimal lowestPrice;

	/**
	 * Рекомендованная цена и экономия
	 * @apiNote nullable
	 * */
	private BigDecimal rrp;
	private Integer savingsValue;

	/** Начальная цена товара (startPrice) для отображения скидки */
	private BigDecimal startPriceForLowestPrice;

	/** Товар находится в состоянии 'С биркой' */
	private boolean isNotUsedYet;

	private boolean isVintage;
	private boolean isOurChoice;

	private int likesCount;
	private boolean isLiked;

	private List<PurchaseRequestedItem> purchaseRequestedItems = Collections.emptyList();

	/**
	 * @return размер вида RUS: 36, 38, 40
	 */
	@JsonIgnore
	@SuppressWarnings("unused") /* используется в представлении catalog/product_card */
	public String getFormattedSize() {

		String joinedSizes = String.join(" / ", sizeSummary.values);

		return String.format("%s: %s", sizeSummary.abbreviation, joinedSizes);
	}

	@AllArgsConstructor
	@Getter @Setter
	public static class SizeSummary {

		/** Значения в конкретной размерной сетке */
		private List<String> values = Collections.emptyList();

		/** Сокращенное название размерной сетки (RUS, INT, FR) */
		private String abbreviation;
	}

	/** Вещи товара, которые ожидают от продавца подтверждения продажи */
	@RequiredArgsConstructor @Getter
	public static class PurchaseRequestedItem {private final Long id; private final Long effectiveOrderId; }
}
