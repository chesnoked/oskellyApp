package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.model.enums.AuthorityName;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Vitaliy Khludeev on 09.08.17.
 */
@RequiredArgsConstructor
@RestController
@PreAuthorize("isFullyAuthenticated()")
@Slf4j
@RequestMapping(value = "/commission")
public class CommissionController {

	private final CommissionService commissionService;

	@GetMapping(value = "/price", params = {"user", "category", "price"})
	public ResponseEntity<?> calculatePriceWithCommissionForUser(
			@RequestParam(value = "user") Long userId,
			@RequestParam(value = "category") Long categoryId,
			@RequestParam(value = "price") BigDecimal priceWithoutCommission,
			@RequestParam(value = "turbo", required = false, defaultValue = "0") boolean isTurbo,
			@RequestParam(value = "newCollection", required = false, defaultValue = "0") boolean isNewCollection,
			UserIdAuthenticationToken token
	) {
		if(!token.hasAuthority(AuthorityName.PRODUCT_MODERATION) && !Objects.equals(userId, token.getUserId())) {
			return Utils.badResponseWithFieldError("error", "У вас нет прав доступа для расчета комиссии для этого пользователя");
		}
		return getCommissionResponseEntity(userId, categoryId, priceWithoutCommission, isTurbo, isNewCollection);
	}

	@GetMapping(value = "/price", params = {"category", "price"})
	public ResponseEntity<?> calculatePriceWithCommissionForCurrentUser(
			@RequestParam(value = "category") Long categoryId,
			@RequestParam(value = "price") BigDecimal priceWithoutCommission,
			@RequestParam(value = "turbo", required = false, defaultValue = "0") boolean isTurbo,
			@RequestParam(value = "newCollection", required = false, defaultValue = "0") boolean isNewCollection,
			UserIdAuthenticationToken token
	) {
		return getCommissionResponseEntity(token.getUserId(), categoryId, priceWithoutCommission, isTurbo, isNewCollection);
	}

	private ResponseEntity<?> getCommissionResponseEntity(Long userId, Long categoryId, BigDecimal priceWithoutCommission, boolean isTurbo, boolean isNewCollection) {
		try {

			return ResponseEntity.ok(commissionService
					.calculatePriceWithCommission(priceWithoutCommission, userId, categoryId, isTurbo, isNewCollection)
					.setScale(2, RoundingMode.UP).toString());

		} catch (CommissionException e) {
			log.error(e.getLocalizedMessage(), e);
			return Utils.badResponseWithFieldError("error", "Ошибка при расчете комиссии");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return Utils.badResponseWithFieldError("error", "Произошла ошибка");
		}
	}
}
