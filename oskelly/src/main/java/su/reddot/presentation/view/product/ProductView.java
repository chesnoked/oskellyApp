package su.reddot.presentation.view.product;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.product.ItemsSummaryBySize;
import su.reddot.presentation.view.SellerView;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductView {

	private Long id;

	/**
	 * Наименование товара
	 */
	private String name;

	/**
	 * Описание товара в блоке продавца
	 */
	private String description;

	/**
	 * Состояние товара
	 */
	private String condition;

	/**
	 * Название родительской категории в единственном числе, именительном падеже.
	 * Это название хоть уже и содержится в наименовании товара, вместе с названием бренда,
	 * но название категории отдельно нужно для его отображения
	 * в всплывающем окне при добавлении товара в корзину.
	 */
	private String category;

	private BreadcrumbsView breadcrumbs;

	private List<DescriptionAttributeView> attributes = Collections.emptyList();

	private String brand;

	private String brandUrl;

	private SizeType sizeType;

	private List<ItemsSummaryBySize> sizes = Collections.emptyList();

	private ProductImage primaryImage;
	private List<ProductImage> additionalImages = Collections.emptyList();

	private SellerView seller;

	private boolean hasDiscount = false;
	private boolean hasDecreasedPrice = false;

	/**
	 * Начальная цена, за которую предполагалось продавать товар
	 */
	private String startPrice;

	/**
	 * Текущая цена товара.
	 */
	private String currentPrice;

	/**
	 * Среднерозничная цена, может не сущестовать
	 * BigInt т.к. в Product так
	 */
	private BigDecimal rrp;
	private int savings;

	/**
	 * Товар уже был добавлен в вишлист
	 */
	private boolean inWishList;

	private Long countUsersAddedToWishList;

	private boolean isLiked;

	private int likesCount;

	private boolean priceSubscription;

	private Long countUsersSubscribedOnPrice;

	private boolean canBeAddedToCart;

	private String reasonWhyItCannotBeAddedToCart;

	private boolean vintage;

	private boolean isNewCollection;

	private String state;

	/**
	 * Элементы, отображаемые рядом с лайками (состояние товара и т.п.)
	 */
	private List<String> badges = new ArrayList<>();

	/**
	 * @return у товара нет и не должно быть размера
	 */
	public boolean hasNoSize() { return sizeType == SizeType.NO_SIZE; }

	/** Данные, касающиеся торга по снижению цены.
	 * null, если торг не предусмотрен. */
	private OfferRelated offerRelated;

	@Getter @Setter @Accessors(chain = true)
	public static class OfferRelated {

		/** Согласованная ранее цена если такая есть. */
		private String negotiatedPrice;

		/** Позволяет ли товар снижение цены в данный момент. */
		private boolean allowsNegotiation;
		private String reasonIfNotNegotiable;
		/** Какой текст выводить на контроле перехода в пространство переговоров. */
		private String negotiationControlText;

		/** История предложений о снижении цены,
		 которые отправил ранее конкретный пользователь (если таковые вообще были) */
		private List<SingleOffer> offersHistory;
	}

	@Getter @Setter @Accessors(chain = true)
	public static class SingleOffer {

		/** Форматированная предложенная цена */
		private String price;

		/** Форматированное время создания предложения */
		private String offeredAt;

		/** Нужно для мобильного приложения */
		private ZonedDateTime zonedOfferedAt;

		/** Реакция продавца на предложение */
	    private Boolean accepted;

		/** Реакция продавца на предложение */
	    private String acceptedMessage;
	}
}
