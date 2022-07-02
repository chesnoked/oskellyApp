package su.reddot.presentation.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import su.reddot.domain.service.promo.gallery.PromoGalleryRequest;

@Slf4j
@Component
public class GalleryValidator implements Validator {
	private static final int EXPECTED_IMAGE_WIDTH = 670;
	private static final int EXPECTED_IMAGE_HEIGHT = 448;
	private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

	/**
	 * Отклонение по размеру по высоте/ширине для требуемого размера фотографии <br/>
	 * допустим, высота фотографии будет EXPECTED_IMAGE_HEIGHT +- (100*delta)%
	 */
	private static final double SIZE_DELTA = 0.20;

	private final DefaultImageValidator imageValidator;

	public GalleryValidator() {
		this.imageValidator = new DefaultImageValidator(MAX_FILE_SIZE, EXPECTED_IMAGE_WIDTH, EXPECTED_IMAGE_HEIGHT, SIZE_DELTA);
	}


	@Override
	public boolean supports(Class<?> aClass) {
		return PromoGalleryRequest.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		PromoGalleryRequest galleryRequest = (PromoGalleryRequest) o;

		//При обновлении элемента галереи мы можем не менять фото, следовательно, валидировать нечего
		if (!(galleryRequest.getImage() == null && galleryRequest.getId() != null)) {
			imageValidator.validate(galleryRequest.getImage(), errors);
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "url not found", "Не указан Url");
	}
}
