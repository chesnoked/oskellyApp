package su.reddot.domain.service.publication.info;

import su.reddot.domain.model.category.PublicationPhotoSample;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.publication.info.view.ProductConditionView;
import su.reddot.domain.service.publication.info.view.PublicationInfoView;
import su.reddot.domain.service.publication.info.view.PublicationSizeTypeView;

import java.util.List;

/**
 * Сервис для получения данных по публикации товара для физических лиц (через страницу публикации)
 */
public interface PublicationInfoService {

	/**
	 * Получить все ранее сохраненые данные по публикации товара
	 *
	 * @param detailedProduct публикуемый товар
	 * @return данные по публикации товара
	 */
	PublicationInfoView getPublicationInfoForProduct(DetailedProduct detailedProduct);

	/**
	 * Вызывается при выборе категории 3 уровня на 2-й странице публикации
	 * Возвращает либо субкатегорию (4 уровень категории), либо все возможные атрибуты
	 *
	 * @return класс, содержащий в себе либо субкатегорию (4 уровень категории), либо все возможные атрибуты
	 */
	PublicationInfoView getSubcategoryOrAttributesForSelectedCategory(Long categoryId);

	List<PublicationSizeTypeView> getSizeTypesForCategory(Long categoryId);

	List<ProductConditionView> getProductConditions(Product product);

	List<PublicationPhotoSample> getPhotoSamplesForCategory(Long categoryId);
}
