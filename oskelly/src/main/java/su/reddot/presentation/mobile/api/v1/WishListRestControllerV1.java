package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.wishlist.WishListException;
import su.reddot.domain.service.wishlist.WishListService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

/**
 * @author Vitaliy Khludeev on 05.10.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/wishlist")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class WishListRestControllerV1 {

	private final WishListService wishListService;

	@PutMapping
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<?> addToWishList(@RequestParam("productId") Long productId, UserIdAuthenticationToken token) {
		try {
			wishListService.addProduct(productId, token.getUserId());
		} catch (WishListException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(e.getLocalizedMessage());
		}

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{productId}")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<?> removeFromWishList(@PathVariable("productId") Long productId, UserIdAuthenticationToken token) {
		try {
			wishListService.removeProduct(productId, token.getUserId());
		} catch (WishListException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(e.getLocalizedMessage());
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/{productId}")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<Boolean> toggle(@PathVariable("productId") Long productId, UserIdAuthenticationToken token) {
		return ResponseEntity.ok(wishListService.toggle(productId, token.getUserId()));
	}
}