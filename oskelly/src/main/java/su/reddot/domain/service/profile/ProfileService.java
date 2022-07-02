package su.reddot.domain.service.profile;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.profile.view.OffersByProduct;
import su.reddot.domain.service.profile.view.ProductSubscription;
import su.reddot.domain.service.profile.view.SoldProduct;

import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 18.06.17.
 */
public interface ProfileService {

	ProfileView getProfileView(Long userId);

	/**
	 * @param sellerId продавец, чей список товаров надо получить
	 * @param productState интересующее состояние товаров
	 * @param nullableViewingUserId необязательный пользователь, который просматривает список товаров
	 * @return список товаров продавца, дополненный данными специально для пользователя, который просматривает эти товары
	 */
	ProductsList getCatalogProductPage(Long sellerId, ProductState productState, Long nullableViewingUserId);

	List<ProductCard> getMyWishList(Long userId);

	List<ProductCard> getMyFavorites(Long userId);

	List<ProductCard> getMyPriceSubscriptions(Long userId);

	void updateAvatar(MultipartFile image, Long userId);

	void updatePersonalInfo(PersonalInfo personalInfo, User u);
	void updateDeliveryRequisite(Delivery delivery, User u);

	void updateSellerInfo(ProfileSellerRequest request, Long userId);

	boolean profileIsExist(Long userId);

	boolean readingUserFollowProfile(Long profileId, Long nullableUserId);

	/** Список предложений снизить цену, которые отправил данный пользователь.
	 * Сначала идут товары, <b>первый</b> оффер по которым самый новый.
	 **/
	List<OffersByProduct> getOffersFrom(User buyer);

	/** Список предложений снизить цену, которые получил данный пользователь-продавец.
	 * Сначала идут товары, <b>первый</b> оффер по которым самый новый.
	 **/
	List<OffersByProduct> getOffersTo(User seller);
	Optional<OffersByProduct> getOffersTo(User seller, Long id);

	/** Перестать следить за обновлением цены на данный товар. */
	void dropPriceTracking(Long productId, User u);

	/** Список товаров, которые ожидают моего подтверждения продажи. */
    List<SoldProduct> getProductsToConfirm(User u);

    List<SoldProduct> getSoldProducts(User u);

    /** Список товаров, которые уже оплачены покупателем, но еще не доставлены до него. */
	List<SoldProduct> getSalePendingProducts(User u);

	/** Список моих заказов. */
    List<OrderView> getOrders(User user);

	/** Список моих подписок на появление в магазине новых товаров с заданными характеристиками. */
    List<ProductSubscription> getProductSubscriptions(User user);

    @Getter @Setter
	class PersonalInfo {

		private String name;

		private String birthDate;

		private String phone;
	}
	@Getter @Setter
	class Delivery {

		private String zipCode;

		private String city;

		private String address;
		private String extensiveAddress;

		private String country;
	}
}
