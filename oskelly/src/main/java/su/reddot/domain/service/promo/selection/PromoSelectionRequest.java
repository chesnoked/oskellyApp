package su.reddot.domain.service.promo.selection;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.promo.PromoSelection;

@Data
public class PromoSelectionRequest {
	private Long id;

	private PromoSelection.PromoGroup promoGroup;

	private String firstLine;
	private String secondLine;
	private String thirdLine;
	private String url;

	private MultipartFile image;
	private String alt;

	private Long orderIndex;
}
