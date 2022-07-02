package su.reddot.domain.service.profile;

import lombok.Data;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.user.User;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Vitaliy Khludeev on 18.06.17.
 */
@Data
public class ProfileView {

	private String email;

	private String registrationTime;

	private String name;

	private String city;

	private String birthDate;

	private String editableBirthDate;

	private Boolean isTrusted;

	private String paymentDetails;
	
	private Integer countSoldProducts;

	private Integer publishedProductsCount;

	private String avatarUrl;

	private String nickname;

	private Boolean isVip;

	private Boolean isPro;

	private String phone;

	private String lastName;

	private String firstName;

	private String postCode;

	private String sellerAddress;

	private String deliveryAddress;

	private Integer subscribers;

	private Integer subscriptions;

	private Integer likes;

	private User user;

	ProfileView(User user, String urlPrefix, Integer countSoldProducts,
				Integer publishedProductsCount, Integer countSubscribers, Integer countSubscriptions, Integer countLikes) {

		this.publishedProductsCount = publishedProductsCount;
		this.subscribers = countSubscribers;
		this.subscriptions = countSubscriptions;
		this.likes = countLikes;

		email = user.getEmail();

		registrationTime = user.getRegistrationTime().format(
				DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"))
		);

		name = user.getName();

		birthDate = user.getBirthDate() != null ?
				user.getBirthDate().format(
						DateTimeFormatter.ofPattern("dd MMMM", new Locale("ru"))
				) :
				null;

		editableBirthDate = user.getBirthDate() != null ?
				user.getBirthDate().format(
						DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru"))
				) :
				null;

		isTrusted = user.getIsTrusted();
		deliveryAddress = user.getDeliveryRequisite() != null ? user.getDeliveryRequisite().humanReadable() : null;
		paymentDetails = user.getPaymentDetails();
		this.countSoldProducts = countSoldProducts;
		isVip = user.getVipStatusTime() != null;
		isPro = user.isPro();
		nickname = user.getNickname();
		sellerAddress = user.getSellerRequisite() != null? user.getSellerRequisite().getAddress() : null;

		avatarUrl = user.getAvatarPath() != null ?
				urlPrefix + user.getAvatarPath() :
				User.noImageUrl;
		this.user = user;

		SellerRequisite sellerRequisite = user.getSellerRequisite();
		if (sellerRequisite != null) {
			firstName =  sellerRequisite.getFirstName();
			lastName =  sellerRequisite.getLastName();
			phone =  sellerRequisite.getPhone();
			city =  sellerRequisite.getCity();
			postCode = sellerRequisite.getZipCode();
		}
	}
}
