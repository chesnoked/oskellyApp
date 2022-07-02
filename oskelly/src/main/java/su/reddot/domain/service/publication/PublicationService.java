package su.reddot.domain.service.publication;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.controller.publication.PublicationPropertiesController;

import java.math.BigDecimal;
import java.util.List;

public interface PublicationService {

	Product publish(PublicationRequest publicationRequest, Long userId) throws PublicationException;

	/**
	 * Обновление черновика при публикации во вкладке "Информация"
	 *
	 * @param product публикуемый товар
	 * @return
	 */
	Product updateProductInfo(Product product, Long categoryId, SizeType sizeType, Long sizeId, List<Long> attributeValues) throws PublicationException;

	Product updateCategory(Product product, Long categoryId) throws PublicationException;

	Product updateProductDescription(Product product, String description, boolean isVintage, String model,
									 String origin,
									 BigDecimal purchasePrice,
									 Integer purchaseYear,
									 String serialNumber) throws PublicationException;


	/**
	 * @return все возможные состояния товара
	 */
	List<ProductCondition> getAllProductConditions();

	Product updateProductConditionAndPrice(Product product, Long conditionId, BigDecimal priceWithCommission) throws PublicationException;

	Product updatePrice(Product product, BigDecimal priceWithCommission) throws PublicationException;

	Product setDiscount(Product product, BigDecimal priceWithDiscount) throws PublicationException;

	Product updateCondition(Product product, Long conditionId) throws PublicationException;

	Product updateOriginAndDescription(Product product, String description, String origin, BigDecimal purchasePrice, Integer purchaseYear) throws PublicationException;

	Product sendToModeration(Product product) throws PublicationException;

	void updateProductPhoto(Product product, MultipartFile photo, int photoOrder) throws PublicationException;

	void updateSellerInfo(Product product,
						  String phone,
						  String firstName,
						  String lastName,
						  String postcode,
						  String city,
						  String address,
						  String extensiveAddress);

	boolean userHasDrafts(Long userId);

	Product updateAttributes(Product product, List<Long> attributes);

	Product updateSize(Product product, SizeType sizeType, Long sizeId) throws PublicationException;

	Product updateSizeType(Product product, SizeType sizeType);

	ResponseEntity<?> updatePhoto(Long productId, PublicationPropertiesController.PublicationPhotoRequest imageContainer, BindingResult bindingResult, UserIdAuthenticationToken token);

	/**
	 * Проверка на то, что товар создал текущий пользователь
	 *
	 * @param product публикуемый товар
	 * @param userId  id текущего пользователя, совершающего действия в системе
	 */
	boolean productBelongsToUser(Product product, Long userId);

	/**
	 * Вызываем для проверки - прошник или нет
	 *
	 * @param userId
	 * @return
	 */
	boolean runProPublication(Long userId);

	List<Integer> getCompletedSteps(Product p);
}
