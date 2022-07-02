package su.reddot.presentation.mobile.api.v1.response.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.profile.ProfileSellerRequest;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 04.09.17.
 */
@RequiredArgsConstructor
@Getter
public class Account {
	private final Long id;
	private final String email;
	private final String avatar;
	private final String nickname;
	private final String city;
	private final String description;
	private final Integer publishedProductsCount;
	private final String phone;
	private final String firstName;
	private final String lastName;
	private final SellerRequisite sellerRequisite;
	private final DeliveryRequisite deliveryRequisite;
	private final String registrationTime;
	private final Integer followersCount;
	private final Integer followingsCount;
	private final Integer likesCount;
	private final String birthDate;
	private final ProfileSellerRequest seller;
	private final boolean pro;
	private final Integer countProductsForSale;
	private final Integer countProductsInWishList;
}
