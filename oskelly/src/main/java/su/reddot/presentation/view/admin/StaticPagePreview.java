package su.reddot.presentation.view.admin;

import lombok.Data;
import lombok.experimental.Accessors;
import su.reddot.domain.model.user.User;

@Data
@Accessors(chain = true)
public class StaticPagePreview {
	private Long id;
	private String url;
	private String name;
	private String text;
	private User modifiedBy;
	private String modifiedAt;
	private String formattedModifiedAt;
	private String tag;
	private String imagePath;
	private String status;
}
