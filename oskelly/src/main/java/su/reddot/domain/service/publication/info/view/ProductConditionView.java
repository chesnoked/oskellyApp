package su.reddot.domain.service.publication.info.view;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProductConditionView {

	private Long id;
	private String name;
	private boolean checked = false;
	private String description;
}
