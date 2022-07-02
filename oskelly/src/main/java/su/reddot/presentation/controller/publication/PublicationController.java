package su.reddot.presentation.controller.publication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.publication.PublicationRequest;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/publication")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class PublicationController {

	private final BrandService brandService;
	private final PublicationService publicationService;
	private final ImageService imageService;

	@GetMapping()
	public String preparePublicationTemplate(UserIdAuthenticationToken token, Model model) {
		if (publicationService.runProPublication(token.getUserId())){
			return "redirect:/admin/moderation";
		}
		model.addAttribute("brands", brandService.getAll());
		boolean hasDrafts = publicationService.userHasDrafts(token.getUserId());
		model.addAttribute("hasDrafts", hasDrafts);

		return "publication";
	}

	@PostMapping()
	public ResponseEntity<?> publish(@Valid PublicationRequest publicationRequest,
	                                 BindingResult bindingResult,
	                                 UserIdAuthenticationToken token) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
		}

		try {
			Product product = publicationService.publish(
					publicationRequest, token.getUserId()
			);

			return ResponseEntity.ok().body(Collections.singletonMap("productId", product.getId()));

		} catch (IllegalArgumentException e) {
			log.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);

		} catch (PublicationException e) {
			log.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(
					"Ошибка публикации",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
