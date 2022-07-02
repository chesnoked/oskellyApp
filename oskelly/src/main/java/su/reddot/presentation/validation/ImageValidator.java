package su.reddot.presentation.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.presentation.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Vitaliy Khludeev on 28.06.17.
 */
@Slf4j
public abstract class ImageValidator implements Validator {

	protected long allowedFileSize;

	protected boolean needSizeChecks;

	/**
	 * @param allowedFileSize максимальный разрешенный размер для заливки файла в байтах
	 */
	public ImageValidator(long allowedFileSize) {
		this.needSizeChecks = false;
		this.allowedFileSize = allowedFileSize;
	}

	public void validate(Object target, Errors errors) {
		if (target == null) {
			errors.rejectValue("image", "image empty", "Отсутствует изображение");
			return;
		}
		MultipartFile file;
		try {
			file = (MultipartFile) target;
		} catch (ClassCastException e) {
			log.error(e.getLocalizedMessage(), e);
			return;
		}

		try {
			Utils.validateFileSize(file, allowedFileSize);
		} catch (MaxUploadSizeExceededException e) {
			log.error("{}. Размер загружаемого файла, байт: {}", e.getLocalizedMessage(), file.getSize());
			errors.rejectValue("image", "wrong file size", "Слишком большой размер загружаемого файла");
		}

		BufferedImage image;
		try {
			image = getBufferedImage(file);
		} catch (IOException e) {
			errors.rejectValue("image", "wrong image format", "Некорректный формат файла");
			return;
		}

		if (image == null) {
			errors.rejectValue("image", "wrong image format", "Некорректный формат файла");
		}
	}

	BufferedImage getBufferedImage(Object target) throws IOException {
		return ImageIO.read(((MultipartFile) target).getInputStream());
	}
}
