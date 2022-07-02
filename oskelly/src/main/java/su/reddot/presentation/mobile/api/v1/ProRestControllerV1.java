package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.pro.ProService;
import su.reddot.domain.service.propublication.ProPublicationService;
import su.reddot.domain.service.propublication.exception.ProPublicationException;
import su.reddot.domain.service.propublication.view.ProPublicationRequest;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.infrastructure.util.ErrorNotification;
import su.reddot.presentation.Utils;
import su.reddot.presentation.controller.publication.PublicationPropertiesController;
import su.reddot.presentation.validation.ProductSizeMappingRequestValidator;

import javax.validation.Valid;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 30.07.17.
 */
@RestController
@RequestMapping("/mobile/api/v1")
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
@Slf4j
public class ProRestControllerV1 {

	private final ProService proService;
	private final UserService userService;
	private final PublicationService publicationService;
    private final ProductSizeMappingRequestValidator productSizeMappingRequestValidator;
    private final ProPublicationService proPublicationService;

	@ResponseBody
	@GetMapping("/getsetllerdata/byid")
	@PreAuthorize("hasAnyAuthority('PRODUCT_MODERATION')")
	public Object getSellerData(@RequestParam("id") Long sellerId, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
		Optional<User> userOptional = userService.getUserById(sellerId);
		if(!userOptional.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		User user = userOptional.get();
		return proService.getSellerInfo(user, page);
	}

	@ResponseBody
	@GetMapping("/getsetllerdata/bynick")
	@PreAuthorize("hasAnyAuthority('PRODUCT_MODERATION')")
	public Object getSellerData(@RequestParam("nick") String nick, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
		Optional<User> userOptional = userService.getUserByNickname(nick);
		if(!userOptional.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		User user = userOptional.get();
		return proService.getSellerInfo(user, page);
	}

	@ResponseBody
	@PostMapping("/uploadimage/{product_id}/{image_number}")
	public Object uploadImage(
			@PathVariable(value = "product_id") Long productId,
			@PathVariable(value = "image_number") Integer imageNumber,
			PublicationPropertiesController.PublicationPhotoRequest imageContainer,
			BindingResult bindingResult,
			UserIdAuthenticationToken token

	) {
		imageContainer.setPhotoOrder(imageNumber); // костыль
		return publicationService.updatePhoto(productId, imageContainer, bindingResult, token);
	}

    @PostMapping("/publish/{sellerId}")
    public ResponseEntity<?> publishProductItems(@PathVariable("sellerId") Long sellerId,
                                                 @RequestBody @Valid ProPublicationRequest request,
                                                 BindingResult bindingResult) {
        productSizeMappingRequestValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Utils.mapErrors(bindingResult));
        }
        ErrorNotification errorNotification = new ErrorNotification();
        try {
            Product product = proPublicationService.publishProduct(request, sellerId, errorNotification);
            if (product == null) {
                return ResponseEntity.badRequest().body(errorNotification.getAll());
            }
            return ResponseEntity.ok(product.getId());
        } catch (ProPublicationException e) {
            log.error(e.getLocalizedMessage(), e);

            return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
        }
    }
}