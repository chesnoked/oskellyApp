package su.reddot.domain.service.promo.selection;

import lombok.Builder;
import lombok.Data;
import su.reddot.domain.model.promo.PromoSelection;

@Data
@Builder
public class PromoSelectionResponse {
	private Long id;

	private PromoSelection.PromoGroup promoGroup;

	private String firstLine;
	private String secondLine;
	private String thirdLine;

	private String img;
	private String alt;

	private String url;

	private Long orderIndex;
}
