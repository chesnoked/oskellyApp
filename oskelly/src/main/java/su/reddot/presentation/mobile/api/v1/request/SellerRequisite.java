package su.reddot.presentation.mobile.api.v1.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaliy Khludeev on 19.10.17.
 */
@RequiredArgsConstructor
@Getter
public class SellerRequisite {
	private final String firstName;
	private final String lastName;
	private final String phone;
	private final String zipCode;
	private final String city;
	private final String address;
}
