package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.promo.gallery.PromoGalleryRequest;
import su.reddot.domain.service.promo.gallery.PromoGalleryResponse;
import su.reddot.domain.service.promo.gallery.PromoGalleryService;
import su.reddot.domain.service.promo.selection.PromoSelectionRequest;
import su.reddot.domain.service.promo.selection.PromoSelectionResponse;
import su.reddot.domain.service.promo.selection.PromoSelectionService;
import su.reddot.domain.service.promo.selection.TooManyPromoException;
import su.reddot.presentation.Utils;
import su.reddot.presentation.validation.GalleryValidator;
import su.reddot.presentation.validation.SelectionValidator;

import java.util.Optional;

@Controller
@RequestMapping("/admin/promo")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_CREATE,"
							+ "T(su.reddot.domain.model.enums.AuthorityName).CONTENT_DELETE)")
public class PromoController {

	private final PromoGalleryService promoGalleryService;
	private final PromoSelectionService promoSelectionService;
	private final GalleryValidator galleryValidator;
	private final SelectionValidator selectionValidator;


	@GetMapping
	public String getPromoAdminPane(Model model) {

		model.addAttribute("galleryItems", promoGalleryService.getAll());
		model.addAttribute("selections", promoSelectionService.getIndexPromo());
		model.addAttribute("favorites", promoSelectionService.getIndexFavorites());
		return "admin/content-management/promo";
	}

	@GetMapping("/gallery/new")
	public String newGallery() {
		return "admin/content-management/gallery";
	}

	@GetMapping("/selection/new")
	public String newSelection() {
		return "admin/content-management/selection";
	}

	@GetMapping("/gallery/{id}")
	public String getPromoGalleryPane(@PathVariable(value = "id", required = false) Long id, Model model) {

		Optional<PromoGalleryResponse> galleryResponse = promoGalleryService.getById(id);
		if (!galleryResponse.isPresent()) {
			return "redirect:/admin/promo/gallery/new";
		}

		model.addAttribute("gallery", galleryResponse.get());

		return "admin/content-management/gallery";
	}


	@GetMapping("/selection/{id}")
	public String getPromoSelectionPane(@PathVariable(value = "id", required = false) Long id, Model model) {
		Optional<PromoSelectionResponse> selectionResponse
				= promoSelectionService.getById(id);
		if (!selectionResponse.isPresent()) {
			return "redirect:/admin/promo/selection/new";
		}

		PromoSelectionResponse selection = selectionResponse.get();
		model.addAttribute("selection", selection);

		return "admin/content-management/selection";
	}

	@PostMapping("/gallery")
	public ResponseEntity<?> createGallery(@ModelAttribute PromoGalleryRequest galleryRequest, BindingResult result) {
		galleryValidator.validate(galleryRequest, result);
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(result));
		}

        try {
            if (galleryRequest.getId() == null) {
                promoGalleryService.create(galleryRequest);
            } else {
                promoGalleryService.update(galleryRequest);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);

            return ResponseEntity.badRequest().body(
            		Utils.fieldError("unexpectedError", "Неожиданная ошибка"));
        }

		return ResponseEntity.ok().build();
	}

	@PostMapping("/selection")
	public ResponseEntity<?> createPromoSelection(@ModelAttribute PromoSelectionRequest selectionRequest, BindingResult result) {
		selectionValidator.validate(selectionRequest, result);
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(result));
		}
        try {
            if (selectionRequest.getId() == null) {
                try {
                    promoSelectionService.create(selectionRequest);
                } catch (TooManyPromoException e) {
                    return ResponseEntity.badRequest().body(
                            Utils.fieldError("promoblocks_count", "Превышено количество возможных промоблоков")
                    );
                }
            } else {
                promoSelectionService.update(selectionRequest);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);

            return ResponseEntity.badRequest().body(
            		Utils.fieldError("unexpectedError", "Неожиданная ошибка"));
        }

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/gallery/{id}")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_DELETE)")
	public ResponseEntity<?> deleteGallery(@PathVariable("id") Long id) {
		try {
			promoGalleryService.delete(id);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/selection/{id}")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_DELETE)")
	public ResponseEntity<?> deletePromoSelection(@PathVariable("id") Long id) {
		try {
			promoSelectionService.delete(id);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return ResponseEntity.ok().build();
	}

	@ModelAttribute(name = "activeTab")
	public void setActiveTab(Model m) {
		m.addAttribute("activeTab", "promo content management");
	}
}
