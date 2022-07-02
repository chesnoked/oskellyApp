package su.reddot.presentation.controller.publication;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Vitaliy Khludeev on 07.09.17.
 */
@Data
public class SellerRequest {
	@NotNull
	private String phone;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String postcode;
	@NotNull
	private String city;
	@NotNull
	private String address;

	private String extensiveAddress;

	private boolean completePublication;
}
