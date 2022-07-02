package su.reddot.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Embeddable
@Getter @Setter @Accessors(chain = true)
public class SellerRequisite {

	private String firstName;

	private String lastName;

	/** Название компании для юридических лиц */
	private String companyName;

    private String phone;

	private String zipCode;

	private String city;

	private String address;

	private String extensiveAddress;

	public String getFullName() {
		return ((companyName != null ? companyName : "") + " "
				+ (lastName != null ? lastName : "") + " "
				+ (firstName != null ? firstName : "")).trim();
	}
}
