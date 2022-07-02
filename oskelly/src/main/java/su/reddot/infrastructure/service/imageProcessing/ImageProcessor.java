package su.reddot.infrastructure.service.imageProcessing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.im4java.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import su.reddot.infrastructure.configuration.ImageProcessingConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static su.reddot.infrastructure.service.imageProcessing.ImageProcessor.ProcessingType.*;

/**
 * @author Vitaliy Khludeev on 14.06.17.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImageProcessor {

	public enum ProcessingType {

		TINY("tiny", 270, 320), ITEM("item", 1080, 1280), SMALL("small", 81, 96), ORIG("orig", 0, 0);

		ProcessingType(String prefix, int width, int height) {
			this.prefix = prefix;
			this.width = width;
			this.height = height;
		}

		@Getter
		private String prefix;
		@Getter
		private int width;
		@Getter
		private int height;

	}

	private final RabbitTemplate rabbitTemplate;

	/**
	 * @param imagePath абсолютный путь к файлу
	 */
	public void addToQueue(String imagePath) {
		rabbitTemplate.convertAndSend(ImageProcessingConfiguration.queueName, imagePath);
	}

	synchronized public void readFromQueue(String imagePath) throws InterruptedException, IOException, IM4JavaException {
		try {
			processImage(imagePath);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			addToQueue(imagePath);
		}
	}

	private void processImage(String imagePath) throws IOException, IM4JavaException, InterruptedException {
		File file = new File(imagePath);
		if (!file.exists()) {
			throw new FileNotFoundException("File " + file.getAbsoluteFile() + " does not exists");
		}
		String path = file.getParent();
		String name = file.getName();
		String origName = "orig-" + name;
		File orig = new File(path, origName);
		Files.copy(file.toPath(), orig.toPath(), StandardCopyOption.REPLACE_EXISTING);

		//cropImage(orig.getPath());

		extentImage(path, origName, name, ITEM);
		extentImage(path, origName, name, TINY);
		extentImage(path, origName, name, SMALL);

		Files.delete(file.toPath());
	}
	// Т.к. физики грузили иногда херню, пинято решение не испльщовать следующеи два метода, а делать EXTENT
	private void resizeImage(String path, String origName, String name, ProcessingType processingType) throws InterruptedException, IOException, IM4JavaException {
		IMOperation imOperation = new IMOperation();
		imOperation.addImage(path + "/" + origName);
		imOperation.resize(processingType.getWidth(), processingType.getHeight());
		imOperation.addImage(path + "/" + processingType.getPrefix() + "-" + name);
		ConvertCmd cmd = new ConvertCmd();
		cmd.run(imOperation);
	}

	private void cropImage(String fullPath) throws IM4JavaException, InterruptedException, IOException {
		ConvertCmd cmd = new ConvertCmd();
		Info info = new Info(fullPath);
		int width = info.getImageWidth();
		int height = info.getImageHeight();
		int x = width / 3;
		int y = height / 4;
		if (x > y) {
			int newWidth = height * 3 / 4;
			int croppedWidth = width - newWidth;
			IMOperation imOperation = new IMOperation();
			imOperation.addImage(fullPath);
			imOperation.crop(newWidth, height, croppedWidth / 2, 0);
			imOperation.addImage(fullPath);
			cmd.run(imOperation);
		} else {
			int newHeight = width * 4 / 3;
			int croppedHeight = height - newHeight;
			IMOperation imOperation = new IMOperation();
			imOperation.addImage(fullPath);
			imOperation.crop(width, newHeight, 0, croppedHeight / 2);
			imOperation.addImage(fullPath);
			cmd.run(imOperation);
		}
	}

	/**
	 * Заменяет изображение на подогнанное под 1080х1280 с сохранением геометрии и с белым полем-надставкой
	 * @param path - путь до файлов
	 * @param origName - имя Эталонного файла загруженног на сервер
	 * @param name - общая часть всех имен
	 * @param processingType - формат к которому приводится изображени
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void extentImage(String path, String origName, String name, ProcessingType processingType) throws IM4JavaException, InterruptedException, IOException {
		// оригинальная команда  IM
		// convert ORIGINAL_IMAGE -auto-orient -resize 1080x1280  -background white -compose Copy -gravity Center -extent 1080x1280 BASE-OUTPUT
		ConvertCmd convertCmd = new ConvertCmd();
		IMOperation operation = new IMOperation();
		operation.addImage(path + "/" + origName);
		operation.autoOrient();
		operation.resize(processingType.getWidth(), processingType.getHeight());
		operation.background("white").compose("Copy").gravity("Center");
		operation.extent(processingType.getWidth(), processingType.getHeight());
		operation.addImage(path + "/" + processingType.getPrefix() + "-" + name);
		convertCmd.run(operation);
	}

	public void addWatermark(String absolutePath) throws Exception {
		/* composite -dissolve 15% -tile -gravity center /home/oskelly/watermark.png $file $file-wm */
		CompositeCmd cmd = new CompositeCmd();
		IMOperation  op = new IMOperation();
		op.dissolve(15)
				.gravity("center")
				.addImage("/home/oskelly/watermark.png")
				.addImage(absolutePath)
				.addImage(absolutePath + "-wm.jpg"); // расширение указать явно, иначе апи ВК не распознает переданный (непустой) файл
		cmd.run(op);




	}
}
