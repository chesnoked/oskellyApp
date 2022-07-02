package su.reddot.presentation.view.admin;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModifierView {
	private String by;
	private String at;
}
