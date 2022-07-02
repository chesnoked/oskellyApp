package su.reddot.presentation.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Vitaliy Khludeev on 28.06.17.
 */
@Slf4j
@Component
public class ProfileImageValidator extends ImageValidator {

	private int minWidth;

	private int minHeight;

	private static final String IMAGE_WRONG_SIZE = "Размер картинки должен быть больше чем %1$dx%2$d";

	public ProfileImageValidator() {
		super(2048 * 1024);
		this.minWidth = 145;
		this.minHeight = 145;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return MultipartFile.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		super.validate(target, errors);
		if(errors.hasErrors()) {
			return;
		}
		BufferedImage image;
		try {
			image = getBufferedImage(target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int currentWidth = image.getWidth();
		int currentHeight = image.getHeight();

		if(currentWidth != currentHeight) {
			errors.rejectValue("image", "Допускаются только квадратные изображения");
		}

		if (currentWidth < minWidth || currentHeight < minHeight) {
			errors.rejectValue("image", "wrong image size", String.format(IMAGE_WRONG_SIZE, minWidth, minHeight));
		}
	}
}
