package su.reddot.presentation.controller.admin;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import su.reddot.presentation.Utils;

import java.math.BigDecimal;

/**
 * @author Vitaliy Khludeev on 26.07.17.
 */
@ControllerAdvice(assignableTypes = ModerationController.class)
@Slf4j
public class ModerationAdvice {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<?> handle(HttpMessageNotReadableException e) {
		if(e.getCause() != null && e.getCause() instanceof InvalidFormatException) {
			InvalidFormatException cause = (InvalidFormatException) e.getCause();
			if(cause.getTargetType().isAssignableFrom(Long.class) || cause.getTargetType().isAssignableFrom(Integer.class) || cause.getTargetType().isAssignableFrom(BigDecimal.class)) {
				return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "Неверное числовое значение: " + cause.getValue()));
			}
		}
		log.error(e.getLocalizedMessage(), e);
		return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<?> handle(MethodArgumentTypeMismatchException e) {
		if(e.getRequiredType().isAssignableFrom(Long.class) || e.getRequiredType().isAssignableFrom(Integer.class) || e.getRequiredType().isAssignableFrom(BigDecimal.class)) {
			return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", "Неверное числовое значение: " + e.getValue()));
		}
		log.error(e.getLocalizedMessage(), e);
		return ResponseEntity.badRequest().body(Utils.fieldError("Ошибка", e.getLocalizedMessage()));
	}
}
