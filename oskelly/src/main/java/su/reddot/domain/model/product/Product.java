package su.reddot.domain.model.product;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;
import su.reddot.domain.dao.LocalDateTimeConverter;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.like.Likeable;
import su.reddot.domain.model.like.ProductLike;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
public class Product implements Likeable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;

    @Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime sendToModeratorTime;

	@ManyToOne
	private Brand brand;

	@ManyToOne
	private Category category;

	@ManyToOne
	private User seller;

	@ManyToOne
	private ProductCondition productCondition;

	/* FIXME не используется (?) */
	private String name;

	@Enumerated(EnumType.STRING)
	private ProductState productState;

	@Enumerated(EnumType.STRING)
	private SizeType sizeType;

	private String description;
	private String paymentDescription;
	private String deliveryDescription;

	//Происхождение товара
	private String origin;

	//Стоимость приобретения
	private BigDecimal purchasePrice;

	//Год приобретения
	private Integer purchaseYear;

	//Одобрени модератором
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime publishTime;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime ourChoiceStatusTime;

	private boolean vintage;

	/**
	 * Модель товара. Временное решение(потом будет справочник)
	 */
	private String model;

	private boolean isTurbo;

	private boolean isNewCollection;

	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Image> images;

	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ProductAttributeValueBinding> attributeValues;

	/**
	 * Артикул
	 */
	private String vendorCode;

	/**
	 * Среднерозничная цена. Используется в Каталоге и в Карточке товара.
	 */
	private BigDecimal rrpPrice;

	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ProductItem> productItems;

	/**
	 * Вещи, которые покупатели уже оплатили в своем заказе, и которые ожидают
	 * согласия продажи со стороны продавца
	 */
	@OneToMany(mappedBy = "product")
	@Where(clause = "state = 'PURCHASE_REQUEST' AND effective_order_id IS NOT NULL")
	private List<ProductItem> purchaseRequestedProductItems;

	/** Все предложения по снижению цены для данного товара
	 * @apiNote следует использовать только в качестве более удобного варианта операции добавления
	 * данных о торге к товару, но не для запроса данных о доступных торгах. */
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.EXTRA)
	private List<Offer> offers;
	public void addOffer(Offer o) {
		offers.add(o);
		o.setProduct(this);
	}

	/**
	 * Проверить, может ли данный пользователь просмотреть товар.
	 *
	 * @param nullableUser аутентифицированный пользователь или гость, если null
	 * @return true если пользователь может просмотреть товар иначе false
	 */
	public boolean canBeViewedByUser(User nullableUser) {

		/* никто не может просмотреть товар - черновик */
		if (productState == ProductState.DRAFT) {
			return false;
		}

	    /* любой модератор (будь то модератор товаров, пользователей, заказов, неважно)
	    * может просмотреть товар-не-черновик */
	    /* TODO нужно переделать логику таким образом, что
	    * только пользователи с правами (AuthorityName) - Модерировать товар и смотреть товар - могут просматривать товары
	    * как модераторы
	    */
		if (nullableUser != null && nullableUser.isModerator()) { return true; }
		if (nullableUser !=  null && nullableUser.canViewAllProducts()) { return true; }

		/* продавец может всегда просмотреть свой товар (не черновик) */
		if (seller.equals(nullableUser)) {
			return true;
		}

		return isAvailable();
	}

	/**
	 * Доступен для отображения покупателям
	 */
	public boolean isAvailable() {
		return productState == ProductState.SOLD
				|| productState == ProductState.PUBLISHED;
	}

	/** "Как новый" */
	public boolean isNotUsedYet() {
		//noinspection ConstantConditions
		return productCondition != null
                // FIXME сильная зависимость от строкового представления
				&& productCondition.getName().equals("С биркой");
	}

	public String getDisplayName() {
		if (category == null) {
			return null;
		}
		String singularName = category.getSingularName();
		if (singularName != null) {
			return singularName;
		} else {
			return category.getDisplayName();
		}
	}

	public void publish() {
		productState = ProductState.PUBLISHED;
		publishTime = LocalDateTime.now(ZoneId.systemDefault());
	}

	public boolean isSold() {
	    return productState == ProductState.SOLD;
	}

	public boolean isOurChoice() {
		return ourChoiceStatusTime != null;
	}

	public boolean isDeleted() { return productState == ProductState.DELETED; }

	@Override
	public Class<ProductLike> getLikeClass() {
		return ProductLike.class;
	}

	public String getImagePreview() {
		return getImages().stream().filter(Image::isMain).findFirst().map(i -> i.getImagePath(ImageProcessor.ProcessingType.TINY)).orElse(null);
	}

	@Override
	public String getUrl() {
		return String.format("/products/%s", id);
	}

	@Override
	public String getEntityName() {
		return "товар";
	}
}

