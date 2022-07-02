package su.reddot.presentation.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import su.reddot.domain.service.promo.selection.PromoSelectionRequest;

@Component
public class SelectionValidator implements Validator {

	private static final int PROMO_TEXT_LENGTH = 30;
	private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
	private DefaultImageValidator imageValidator;

	public SelectionValidator() {
		this.imageValidator = new DefaultImageValidator(MAX_FILE_SIZE);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PromoSelectionRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//фотки не проверяем, на стороне клиента она скукожится
		PromoSelectionRequest promoSelection = (PromoSelectionRequest) target;

		if (!(promoSelection.getImage() == null && promoSelection.getId() != null)) {
			imageValidator.validate(promoSelection.getImage(), errors);
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstLine", "firstLine not found", "Не указана первая строка");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondLine", "secondLine not found", "Не указана вторая строка");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thirdLine", "thirdLine not found", "Не указана третья строка");

		if (promoSelection.getFirstLine() != null && promoSelection.getFirstLine().length() > PROMO_TEXT_LENGTH) {
			errors.rejectValue("firstLine", "firstLine is too long", "Первая строка слишком длинная");
		}
		if (promoSelection.getSecondLine() != null && promoSelection.getSecondLine().length() > PROMO_TEXT_LENGTH) {
			errors.rejectValue("secondLine", "secondLine is too long", "Вторая строка слишком длинная");
		}
		if (promoSelection.getThirdLine() != null && promoSelection.getThirdLine().length() > PROMO_TEXT_LENGTH) {
			errors.rejectValue("thirdLine", "thirdLine is too long", "Третья строка слишком длинная");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "url not found", "Не указан Url");

	}
}
