package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.Discount;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 17.08.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/orders")
@Slf4j
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class OrderRestControllerV1 {

	private final OrderService orderService;

	private final UserService userService;

	@PostMapping("/{id}/hold")
	public ResponseEntity<?> initHold(@PathVariable Long id, UserIdAuthenticationToken token) {

		String paymentRequest = orderService.initHold(id, userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new));
		return ResponseEntity.ok(Collections.singletonMap("paymentRequest", paymentRequest));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOrder(@PathVariable Long id, UserIdAuthenticationToken token) {
		User u = userService.getUserById(token.getUserId()).orElseThrow(IllegalArgumentException::new);
		Optional<OrderView> order = orderService.getUserOrder(id, u);
		// TODO бросать осмысленное исключение вроде OrderNotFound с информацией для страницы 404
		order.orElseThrow(IllegalArgumentException::new);
		OrderView existingOrder = order.orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(existingOrder);
	}

	@PutMapping("/{id}/delivery")
	public ResponseEntity<?> setDeliveryRequisite(
			@PathVariable Long id,
			@RequestBody DeliveryRequisite r,
			@RequestParam(defaultValue = "false") boolean updateProfile,
			UserIdAuthenticationToken t
	) {
		orderService.setDeliveryRequisite(id, r, userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new));
		if (updateProfile) { userService.setDeliveryRequisite(t.getUserId(), r); }
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/discount")
	public Discount addDiscount(@PathVariable Long id, @RequestParam String code, UserIdAuthenticationToken t)
			throws OrderException, NotFoundException, DiscountIsAlreadyUsedException {

		User u = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		orderService.addDiscount(id, u, code);
		return orderService.getUserOrder(id, u).orElseThrow(IllegalArgumentException::new).getAppliedDiscount();
	}
}
