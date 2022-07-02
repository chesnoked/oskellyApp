package su.reddot.domain.service.image;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import su.reddot.domain.dao.ImageRepository;
import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor.ProcessingType;
import su.reddot.infrastructure.util.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static su.reddot.infrastructure.service.imageProcessing.ImageProcessor.ProcessingType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultImageService implements ImageService {
	private static final String PRODUCT_IMG_DIR = "product/%1$s/";

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String imageUrlPrefix;

	@Value("${resources.images.pathToDir}")
	@Setter
	private String imageBaseDirPath;

	private final ImageRepository imageRepository;
	private final ImageProcessor imageProcessor;

	@Override
	public List<ProductImage> getProductImages(Product product) {

		List<Image> rawImages = imageRepository.findByProductWithLimit(product, 100);

        /* Список действительно существующих из всех найденных изображений продукта */
		List<ProductImage> cookedProductImages = new ArrayList<>();

		for (Image rawImage : rawImages) {
			ProductImage productImage = of(rawImage);
			cookedProductImages.add(productImage);
		}

		boolean productImagesHasPrimaryImage
				= cookedProductImages.stream().filter(ProductImage::isPrimary).count() > 0;

		if (!productImagesHasPrimaryImage) {
			log.warn("Основное изображение не найдено для товара с id: {}", product.getId());

			if (!cookedProductImages.isEmpty()) {
				cookedProductImages.get(0).setPrimary(true);
			}
		}

		return cookedProductImages;
	}

	@Override
	public List<Image> getRawImages(Product product) {
		return imageRepository.findByProductWithLimit(product, 1000);
	}

	@Override
	public Optional<ProductImage> getPrimaryImage(Product p) {
		Optional<Image> optionalPrimaryImage = imageRepository.findFirstByProductAndIsMainTrue(p);
		if (!optionalPrimaryImage.isPresent()) {
			return Optional.empty();
		}

		Image primaryImage = optionalPrimaryImage.get();

		ProductImage cookedImage = of(primaryImage);

		return Optional.of(cookedImage);
	}

	@Override
	public void saveImageByProduct(Product product, MultipartFile photo, int photoOrder) {
		/*
		  1) определяем путь к директории файла
          2) сохраняем файл
          3) пишем в репозиторий имя файла с путем
         */

		//директория относительно папки со статик файлами
		String productImgDirectory = String.format(PRODUCT_IMG_DIR, product.getId());

		//директория включая папку со статик файлами
		String fullImgDirectory = imageBaseDirPath + productImgDirectory;
		//сохраняем файл
		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(fullImgDirectory, photo);
		String absolutePath = savedFile.getAbsolutePath();

		imageProcessor.addToQueue(absolutePath);

		Image image = new Image();
		image.setProduct(product);
		image.setMain(photoOrder == 1);
		image.setImagePath(productImgDirectory + savedFile.getName());
		image.setPhotoOrder(photoOrder);

		imageRepository.save(image);
	}

	@Override
	@Transactional
	public void deleteImage(Long id) {
		Image image = imageRepository.findOne(id);
		String path = image.getImagePath();
		deleteImageWithPrefix(path, TINY);
		deleteImageWithPrefix(path, SMALL);
		deleteImageWithPrefix(path, ITEM);
		deleteImageWithPrefix(path, ORIG);
		imageRepository.delete(image);
	}

	private void deleteImageWithPrefix(String imagePath, ProcessingType processingType) {
		File processedImageFile = getProcessedImageFile(imagePath, processingType);
		boolean deleteSuccess = processedImageFile.delete();
		if (!deleteSuccess) {
			log.error("Не получилось удалить файл " + processedImageFile.getAbsolutePath());
		}
	}

	@Override
	public Optional<ProductImage> getImageByProductAndPhotoOrder(Product product, int photoOrder) {
		Optional<Image> image = imageRepository.findFirstByProductAndPhotoOrder(product, photoOrder);
		return image.map(this::of);
	}

	private boolean imageExists(String relativeImagePath) {
		Path imagePath = Paths.get(imageBaseDirPath, relativeImagePath);
		return Files.exists(imagePath);
	}

	private ProductImage of(Image rawImage) {
		String imagePath = rawImage.getImagePath();

		ProductImage cookedImage = new ProductImage();
		cookedImage.setId(rawImage.getId());
		cookedImage.setPhotoOrder(rawImage.getPhotoOrder());

		File originFile = getProcessedImageFile(imagePath, ORIG);
		/* Установить ссылку на основное изображение */
		if (originFile.exists()) {
			cookedImage.setUrl(imageUrlPrefix + getRelativeProcessedFilePath(imagePath, TINY));
			cookedImage.setLargeImageUrl(imageUrlPrefix + getRelativeProcessedFilePath(imagePath, ITEM));
			cookedImage.setSmallImageUrl(imageUrlPrefix + getRelativeProcessedFilePath(imagePath, SMALL));
			cookedImage.setOriginalImageUrl(imageUrlPrefix + getRelativeProcessedFilePath(imagePath, ORIG));
		} else {
			log.error("Изображение не найдено: {}", originFile.getAbsolutePath());
		}

		cookedImage.setPrimary(rawImage.isMain() || rawImage.getPhotoOrder() == 1);

		return cookedImage;
	}

	private String getRelativeProcessedFilePath(String imagePath, ProcessingType processingType) {
		String[] imagePathParts = imagePath.split("/");
		String filename = imagePathParts[imagePathParts.length - 1];
		filename = processingType.getPrefix() + "-" + filename;
		imagePathParts[imagePathParts.length - 1] = filename;
		return StringUtils.join(imagePathParts, "/");
	}

	/**
	 * @param imagePath относительный путь от папки со статикой
	 * @return файл
	 */
	private File getProcessedImageFile(String imagePath, ProcessingType processingType) {
		File file = new File(imageBaseDirPath + imagePath);
		File directory = file.getParentFile();
		String filename = processingType.getPrefix() + "-" + file.getName();
		return new File(directory, filename);
	}
}
