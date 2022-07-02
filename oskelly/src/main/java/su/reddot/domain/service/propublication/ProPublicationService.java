package su.reddot.domain.service.propublication;

import su.reddot.domain.model.product.Product;
import su.reddot.domain.service.propublication.exception.ProPublicationException;
import su.reddot.domain.service.propublication.view.ProPublicationGridInfo;
import su.reddot.domain.service.propublication.view.ProPublicationRequest;
import su.reddot.infrastructure.util.ErrorNotification;

public interface ProPublicationService {

	/**
	 * Опубликовать партию товаров как PRO-давец.
	 * Партия товаров - это вид товара (Product) и один или несколько ProductItem-ов.
	 *
	 * @param request           набор данных для публикации
	 * @param errorNotification возможные ошибки при заполнении поля
	 * @param sellerId          id продавца товара в системе
	 * @return опубликованный вид товара
	 */
	Product publishProduct(ProPublicationRequest request, Long sellerId, ErrorNotification errorNotification) throws ProPublicationException;

	/**
	 * Получить данные для генерации формы добавления товара через PRO-публикацию.
	 *
	 * @param categoryId категория, потомками которой будут публикуемые товары
	 * @return данные для генерации формы добавления товара
	 */
	ProPublicationGridInfo getPublicationGridInfo(Long categoryId);
}
