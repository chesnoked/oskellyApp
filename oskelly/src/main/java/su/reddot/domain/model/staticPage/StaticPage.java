package su.reddot.domain.model.staticPage;

import lombok.Data;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class StaticPage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Ссылка, по которой будет доступна страница (начиная от корня)
	 */
	private String url;

	/**
	 * Наименование страницы
	 */
	private String name;

	/**
	 * HTML-контент страницы
	 */
	private String content;

	/**
	 * Подмножество статических страниц
	 */
	@Enumerated(EnumType.STRING)
	private StaticPageGroup pageGroup;

	private String metaDescription;
	private String metaKeywords;

	/**
	 * Id того, кто внес изменения в систему
	 */
	@ManyToOne
	private User modifiedBy;

	/**
	 * Когда внес изменения в систему
	 */
	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime modifiedAt;

	/**
	 * Заголовочное фото статьи
	 */
	private String imagePath;

	@ManyToOne
	private StaticPageTag tag;

	@Enumerated(value = EnumType.STRING)
	private StaticPageStatus status;


	private String pageStyle = "infoPage";
    /**
     * такой специальный костыль - если это RawHTML - это это лендосик и редактировать его нельзя
     */
	private boolean isRawHtml = false;
}
