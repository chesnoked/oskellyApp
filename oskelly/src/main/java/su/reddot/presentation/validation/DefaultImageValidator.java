package su.reddot.presentation.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
public class DefaultImageValidator extends ImageValidator {

	private static final String IMAGE_WRONG_SIZE = "Размер картинки должен быть %1$dx%2$d";

	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;

	private int expectedWidth;
	private int expectedHeight;

	/**
	 * @param width           требуемая ширина
	 * @param height          требуемая высота
	 * @param delta           отклонение (>0 и <1)
	 * @param allowedFileSize максимальный разрешенный размер для заливки файла в байтах
	 */
	DefaultImageValidator(long allowedFileSize, int width, int height, double delta) {
		super(allowedFileSize);
		if (delta <= 0 || delta > 1) {
			throw new IllegalArgumentException();
		}

		this.expectedHeight = height;
		this.expectedWidth = width;
		minWidth = (int) (width - width * delta);
		maxWidth = (int) (width + width * delta);
		minHeight = (int) (height - height * delta);
		maxHeight = (int) (height + height * delta);
		needSizeChecks = true;
	}

	/**
	 * @param allowedFileSize максимальный разрешенный размер для заливки файла в байтах
	 */
	public DefaultImageValidator(long allowedFileSize) {
		super(allowedFileSize);
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
		if (needSizeChecks) {
			checkImageSize(image, errors);
		}
	}

	private void checkImageSize(BufferedImage image, Errors errors) {
		int currentWidth = image.getWidth();
		int currentHeight = image.getHeight();

		if (currentWidth < minWidth || currentWidth > maxWidth ||
				currentHeight < minHeight || currentHeight > maxHeight) {
			errors.rejectValue("image", "wrong image size", String.format(IMAGE_WRONG_SIZE, expectedWidth, expectedHeight));
		}
	}


}
