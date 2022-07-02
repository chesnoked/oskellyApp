package su.reddot.domain.service.promo.gallery;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PromoGalleryRequest {

	private Long id;

	private String url;

	private MultipartFile image;

	private Long orderIndex;
}
