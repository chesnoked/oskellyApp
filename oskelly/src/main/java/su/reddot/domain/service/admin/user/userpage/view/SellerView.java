package su.reddot.domain.service.admin.user.userpage.view;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import su.reddot.domain.model.user.User;

@Data
public class SellerView {

	private User.UserType userType;

	private SellerRequisite sellerRequisite;

	private boolean pro;

	private String bik;
	private String correspondentAccount;
	private String paymentAccount;

	@Getter
	@Setter
	public static abstract class SellerRequisite {
		//адрес приемки товара
		protected String address;
		//фио / полное наименование
		protected String name;
	}

	@Getter
	@Setter
	public static class SimpleUserRequisite extends SellerRequisite {
		private String passport;
		private String inn;
	}

	@Getter
	@Setter
	public static class IPRequisite extends SellerRequisite {
		private String inn;
		private String ogrnip;
	}

	@Getter
	@Setter
	public static class OOORequisite extends SellerRequisite {
		private String inn;
		private String kpp;
		private String orgn;
	}
}
