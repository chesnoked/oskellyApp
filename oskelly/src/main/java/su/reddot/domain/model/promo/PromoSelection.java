package su.reddot.domain.model.promo;

import lombok.Data;

import javax.persistence.*;

/**
 * Модель, содержащая информацию о рекламном блоке
 * Блок используется в:
 *   - промоблоках на главной
 *   - наши фавориты
 *   - ~~ todo промоболоки в каталоге~~
 * Содержит группу, 3 строки с текстом (для стилизации), картинку, альт на картинку и ссылку
 * Дополнительно содержит порядок следования
 */
@Data
@Entity
public class PromoSelection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private PromoGroup promoGroup;

	private String firstLine;
	private String secondLine;
	private String thirdLine;
	private String imageName;
	private String alt;
	private String url;

	/**
	 * Порядок элемента на странице
	 */
	private long orderIndex;

	/**
	 * Группы тоаров
	 */
	public enum PromoGroup{
		/**
		 * 3 промоблока на главной
		 */
		INDEX_PROMO,

		/**
		 * Фавориты на главной
		 */
		FAVORITES

	}
}
