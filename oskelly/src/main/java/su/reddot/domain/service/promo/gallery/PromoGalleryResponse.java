package su.reddot.domain.service.promo.gallery;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoGalleryResponse {
	private Long id;
	private String url;
	private String img;
	private Long orderIndex;
}
