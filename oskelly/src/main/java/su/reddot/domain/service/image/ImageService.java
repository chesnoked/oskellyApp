package su.reddot.domain.service.image;

import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ImageService {

	/**
	 * Получить список всех изображений данного товара.
	 *
	 * @param product товар, для которого нужно получить список его изображений
	 * @return список изображений данного товара
	 */
	List<ProductImage> getProductImages(Product product);
	List<Image> getRawImages(Product product);

	/**
	 * Получить основное изображение товара
	 *
	 * @param p товар, для которого нужно получить основное изображение
	 * @return данные об основном изображении товара
	 */
	Optional<ProductImage> getPrimaryImage(Product p);

	void saveImageByProduct(Product product, MultipartFile photo, int photoOrder);

	void deleteImage(Long id);

	Optional<ProductImage> getImageByProductAndPhotoOrder(Product product, int photoOrder);
}
