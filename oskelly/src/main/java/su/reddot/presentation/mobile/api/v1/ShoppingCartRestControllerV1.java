package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.CartService;
import su.reddot.domain.service.cart.CartView;
import su.reddot.domain.service.cart.OrderCreationResult;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;

import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 16.08.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/cart")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class ShoppingCartRestControllerV1 {

    private CartService<CartView> shoppingCartService;
	private final UserRepository userRepository;
	private final OrderService orderService;


	@Autowired
	public void setShoppingCartService(@Qualifier("shoppingCartService") CartService<CartView> cs) {
		shoppingCartService = cs;
	}


	@PostMapping
	public ResponseEntity<?> addItem(@RequestParam Long productId, @RequestParam Long sizeId, UserIdAuthenticationToken token) {

		User loggedInUser = userRepository.findOne(token.getUserId());
		Long itemId;
		try {
			itemId = shoppingCartService.addItem(productId, sizeId, loggedInUser);
		} catch (ProductNotFoundException e) {
			log.warn(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("productId", "Товар не найден"));
		} catch (ProductCanNotBeAddedToCartException e) {
			log.warn(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("productId", e.getLocalizedMessage()));
		}

		return ResponseEntity.ok(Collections.singletonMap("id", itemId));
	}

	/**
	 * Удалить элемент из корзины
	 * @return актуальное состояние корзины
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeItem(@PathVariable Long id, UserIdAuthenticationToken token) {
		User loggedInUser = userRepository.findOne(token.getUserId());
		shoppingCartService.removeItem(id, loggedInUser);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/order")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> createOrder(@RequestParam List<Long> cartItems, UserIdAuthenticationToken token) {
		User loggedInUser = userRepository.findOne(token.getUserId());
		OrderCreationResult order = shoppingCartService.createOrder(cartItems, loggedInUser);
		return ResponseEntity.ok().body(Collections.singletonMap("id", order.order.getId()));
	}

	@PostMapping("/order2")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<OrderView> createAndGetOrder(@RequestParam List<Long> cartItems, UserIdAuthenticationToken token) {
		User loggedInUser = userRepository.findOne(token.getUserId());
		OrderCreationResult order = shoppingCartService.createOrder(cartItems, loggedInUser);
		OrderView existingOrder = orderService.getUserOrder(order.order.getId(), loggedInUser).get();
		return ResponseEntity.ok(existingOrder);
	}

	@GetMapping
	public ResponseEntity<?> getCart(UserIdAuthenticationToken token) {
		User loggedInUser = userRepository.findOne(token.getUserId());
		return ResponseEntity.ok(shoppingCartService.getCart(loggedInUser));
	}
}
