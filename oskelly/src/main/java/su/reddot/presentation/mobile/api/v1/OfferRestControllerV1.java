package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.product.PriceNegotiationException;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.view.product.ProductView;

import java.math.BigDecimal;

/**
 * @author Vitaliy Khludeev on 28.12.17.
 */
@Controller
@RequestMapping(value = "/mobile/api/v1/offers")
@RequiredArgsConstructor
public class OfferRestControllerV1 {

	private final UserService userService;
	private final ProductService productService;

	/** Предложить новую цену в рамках торга. */
	@PostMapping("/product/{id}")
	public ResponseEntity<ProductView.OfferRelated> makeOffer(@PathVariable Long id, @RequestParam String price, UserIdAuthenticationToken t)
			throws NotFoundException, PriceNegotiationException {
		BigDecimal validatedNewPrice;
		try { validatedNewPrice = new BigDecimal(price); }
		catch (NumberFormatException e) { throw new IllegalArgumentException("Цена должна иметь числовое значение (например, 100 или 200,70)"); }
		User user = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		productService.makeAnOffer(id, validatedNewPrice, user);
		ProductView.OfferRelated offerRelated = productService.getProductView(id, user).map(ProductView::getOfferRelated).orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(offerRelated);
	}
}
