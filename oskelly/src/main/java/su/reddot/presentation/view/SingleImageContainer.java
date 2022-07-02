package su.reddot.presentation.view;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SingleImageContainer {
	private MultipartFile image;
}
