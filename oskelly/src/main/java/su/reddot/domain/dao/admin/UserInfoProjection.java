package su.reddot.domain.dao.admin;

import org.springframework.beans.factory.annotation.Value;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.user.PaymentRequisite;
import su.reddot.domain.model.user.User.UserType;

import java.time.LocalDateTime;

public interface UserInfoProjection {

	Long getId();

	String getNickname();

	@Value("#{target.sellerRequisite?.phone}")
	String getPhone();

	String getEmail();

	LocalDateTime getActivationTime();

	LocalDateTime getProStatusTime();

	LocalDateTime getVipStatusTime();

	Boolean getIsTrusted();

	@Value("#{target.sellerRequisite?.firstName}")
	String getFirstName();

	@Value("#{target.sellerRequisite?.lastName}")
	String getLastName();

	LocalDateTime getRegistrationTime();

	String getSex();

	LocalDateTime getBirthDate();

	@Value("#{target.sellerRequisite?.city}")
	String getCity();

	DeliveryRequisite getDeliveryRequisite();

	String getAvatarPath();

	@Value("#{target.sellerRequisite?.address}")
	String getAddress();

	UserType getUserType();

	String getPassport();

	String getOGRNIP();

	String getOGRN();

	String getINN();

	String getKPP();

	@Value("#{target.sellerRequisite?.companyName}")
	String getCompanyName();

	PaymentRequisite getPaymentRequisite();

}
