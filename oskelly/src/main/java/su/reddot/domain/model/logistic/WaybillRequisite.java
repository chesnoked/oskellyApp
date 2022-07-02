package su.reddot.domain.model.logistic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

/**
 * @author Vitaliy Khludeev on 28.08.17.
 */
@Embeddable
@Getter
@Setter
@Accessors(chain = true)
public class WaybillRequisite {

	private String phone;

	private String zipCode;

	private String name;

	private String address;
}
