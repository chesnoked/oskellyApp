package su.reddot.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

	private Utils() {}

	public static Map<String, String> mapErrors(BindingResult bindingResult) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return errors;
	}

	public static Map<String, String> fieldError(String field, String text) {
		return Collections.singletonMap(field, text);
	}

	public static ResponseEntity<?> badResponseWithFieldError(String field, String errorText) {
		return ResponseEntity.badRequest().body(fieldError(field, errorText));
	}

	/**
	 * Размер файла в байтах
	 *
	 * @param file        заливаемый файл
	 * @param maxFileSize максимальный размер файла в байтах
	 * @throws MaxUploadSizeExceededException при превышении допустимого размера файла
	 */
	public static void validateFileSize(MultipartFile file, long maxFileSize) throws MaxUploadSizeExceededException {
		if (file.getSize() > maxFileSize) {
			throw new MaxUploadSizeExceededException(maxFileSize);
		}
	}

	public static String redirectToErrorPage(RedirectAttributes redirectAttributes, String errorMessage) {
		redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
		return "redirect:/fail";
	}

	public static String prettyRoundToTens(BigDecimal rowPrice) {
		if(rowPrice == null) {
			return null;
		}
		return formatPrice(rowPrice.setScale(-1, RoundingMode.UP));
	}

	public static String prettyRoundToCents(BigDecimal rowPrice) {
		if(rowPrice == null) {
			return null;
		}
		return formatPrice(rowPrice.setScale(2, RoundingMode.UP));
	}

	/** Разделять каждые три разряда пробелом */
	public static String formatPrice(BigDecimal value) {

		DecimalFormat        f = new DecimalFormat("###,###,###.#####");
		DecimalFormatSymbols s = new DecimalFormatSymbols();

		s.setGroupingSeparator(' ');
		s.setDecimalSeparator(',');
		f.setDecimalFormatSymbols(s);

		return f.format(value);
	}

	/**
     * Получить правильное склонение существительного для данного числа:
	 * 21 товар(), 22 товар(а), 29 товар(ов)
	 * @param number число, для которого нужно выбрать склонение существительного
	 * @param declensions массив, первый элемент которого содержит существительное в им.п. ед.ч.,
	 *                    второй - в род.п. ед.ч.,
	 *                    третий - в род.п. мн.ч.
	 * @return подходящее для данного числа склонение
	 */
	public static String getRightDeclension(int number, List<String> declensions) {

		int divisionBy10Remainder = number % 10;
		int divisionBy100Remainder = number % 100;

		return divisionBy10Remainder == 1 && divisionBy100Remainder != 11? declensions.get(0)
				: divisionBy10Remainder > 1 && divisionBy10Remainder < 5
					&& (divisionBy100Remainder < 12 || divisionBy100Remainder > 14)? declensions.get(1)
				: declensions.get(2);

	}
}
