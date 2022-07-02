package su.reddot.domain.service.image;

import lombok.Data;

/**
 * Данные о конкретном изображении товара
 */
@Data
public class ProductImage {

	private Long id;

	/**
	 * Tiny картинка (для главной, каталога, выбранной фотки страницы товара)
	 */
	private String url;

	/**
	 * Для превью на странице товара сбоку от выбранной картинки
	 */
	private String smallImageUrl;

	/**
	 *
	 */
	private String largeImageUrl;

	/**
	 * На сервере может храниться оригинал изображения
	 */
	private String originalImageUrl;

	/**
	 * Изображение "представляет" товар и является основным.
	 */
	private boolean isPrimary;

	private int photoOrder;

	/**
	 * TODO: сделал метод для того, чтобы все шаблоны, которые использовали ранее поле thumbnailUrl, не поломались.
	 * Желательно все шаблоны перевести на корректные ссылки
	 * Возвращает изображение
	 *
	 * @return
	 */
	public String getThumbnailUrl() {
		return url;
	}
}
