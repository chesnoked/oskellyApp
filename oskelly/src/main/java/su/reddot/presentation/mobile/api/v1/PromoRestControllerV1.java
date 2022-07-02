package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.service.promo.gallery.PromoGalleryService;

/**
 * @author Vitaliy Khludeev on 04.09.17.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mobile/api/v1/promo")
@PreAuthorize("isFullyAuthenticated()")
public class PromoRestControllerV1 {

	private final PromoGalleryService promoGalleryService;

	@GetMapping(value = "/gallery")
	public ResponseEntity<?> getGallery() {
		return ResponseEntity.ok(promoGalleryService.getAll());
	}
}
