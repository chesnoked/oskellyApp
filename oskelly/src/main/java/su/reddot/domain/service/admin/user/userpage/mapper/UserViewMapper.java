package su.reddot.domain.service.admin.user.userpage.mapper;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.admin.UserInfoProjection;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.user.PaymentRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.admin.user.userpage.view.ProfileView;
import su.reddot.domain.service.admin.user.userpage.view.SellerView;
import su.reddot.domain.service.admin.user.userpage.view.UserView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserViewMapper {

	private final static DateTimeFormatter registrationFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
	private final static DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;

	public UserView mapUserProjection(UserInfoProjection projection) {
		if (projection == null) {
			return null;
		}
		UserView view = new UserView();
		view.setUserId(projection.getId())
				.setNickname(projection.getNickname()).setEmail(projection.getEmail())
				.setEmailConfirmed(projection.getActivationTime() != null)
				.setStatus(getStatuses(projection).stream().collect(Collectors.joining(" ")))
				.setRegistrationDate(projection.getRegistrationTime().format(registrationFormat));

		String firstName = projection.getFirstName() != null ? projection.getFirstName() : "%ИМЯ%";
		String lastName = projection.getLastName() != null ? projection.getLastName() : "%ФАМИЛИЯ%";
		String name = firstName + " " + lastName;
		view.setName(name);

		view.setPhone(projection.getPhone());

		view.setProfile(getProfileView(projection));
		view.setSeller(getSellerView(projection, name));


		String avatar = projection.getAvatarPath() != null ?
				urlPrefix + projection.getAvatarPath() :
				null;
		view.setAvatar(avatar);

		return view;
	}

	private SellerView getSellerView(UserInfoProjection projection, String name) {
		SellerView sellerView = new SellerView();
		User.UserType userType = projection.getUserType();
		sellerView.setUserType(userType);
		PaymentRequisite paymentRequisite = projection.getPaymentRequisite();
		sellerView.setPro(projection.getProStatusTime() != null);
		if (paymentRequisite != null) {
			sellerView.setBik(paymentRequisite.getBIK());
			sellerView.setCorrespondentAccount(paymentRequisite.getCorrespondentAccount());
			sellerView.setPaymentAccount(paymentRequisite.getPaymentAccount());
		}
		switch (userType) {
			case SIMPLE_USER:
				SellerView.SimpleUserRequisite simpleRequisite = new SellerView.SimpleUserRequisite();
				simpleRequisite.setInn(projection.getINN());
				simpleRequisite.setPassport(projection.getPassport());
				sellerView.setSellerRequisite(simpleRequisite);
				break;
			case IP:
				SellerView.IPRequisite ipRequisite = new SellerView.IPRequisite();
				ipRequisite.setInn(projection.getINN());
				ipRequisite.setOgrnip(projection.getOGRNIP());
				sellerView.setSellerRequisite(ipRequisite);
				break;
			case OOO:
				SellerView.OOORequisite oooRequisite = new SellerView.OOORequisite();
				oooRequisite.setInn(projection.getINN());
				oooRequisite.setKpp(projection.getKPP());
				oooRequisite.setOrgn(projection.getOGRN());
				sellerView.setSellerRequisite(oooRequisite);
				break;
		}
		sellerView.getSellerRequisite().setAddress(projection.getAddress());
		sellerView.getSellerRequisite().setName(name);
		return sellerView;
	}

	private ProfileView getProfileView(UserInfoProjection projection) {
		ProfileView profileView = new ProfileView();
		//TODO: сделать поле active, когда у нас в пользователе появится это поле
		profileView.setActive(true)
				.setSex(getSex(projection))
				.setBirthday(projection.getBirthDate() != null ? projection.getBirthDate().format(birthdayFormat) : null)
				.setSellerCity(projection.getCity());

		DeliveryRequisite deliveryRequisite = projection.getDeliveryRequisite();
		if (deliveryRequisite != null) {
			profileView.setDeliveryAddress(deliveryRequisite.getDeliveryAddress())
					.setDeliveryCity(deliveryRequisite.getDeliveryCity())
					.setDeliveryPostcode(deliveryRequisite.getDeliveryZipCode());
		}
		return profileView;
	}

	private List<String> getStatuses(UserInfoProjection projection) {
		ArrayList<String> statuses = new ArrayList<>();
		if (projection.getProStatusTime() != null) {
			statuses.add("PRO");
		}
		if (projection.getVipStatusTime() != null) {
			statuses.add("VIP");
		}
		if (projection.getIsTrusted() != null) {
			statuses.add("Trusted");
		}
		return statuses;
	}

	private String getSex(UserInfoProjection projection) {
		if (projection.getSex() == null) {
			return "Не указан";
		}
		try {
			User.Sex sexEnum = User.Sex.valueOf(projection.getSex());
			return sexEnum.getDisplayName();
		} catch (IllegalArgumentException e) {
			return "Не указан";
		}
	}
}
