package su.reddot.domain.model.promo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Модель, содержащая информацию о рекламе в фотогалерее
 * Содержит картинку, при нажатии на картинку должен быть переход по ссылке
 */
@Data
@Entity
@NoArgsConstructor
public class PromoGallery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Ссылка
	 */
	private String url;

	/**
	 * Имя картинки
	 */
	private String imageName;

	/**
	 * Порядок элемента на странице
	 */
	private long orderIndex;

	public PromoGallery(String url, String imageName, Long orderIndex) {
		this.url = url;
		this.imageName = imageName;
		this.orderIndex = orderIndex;
	}
}
