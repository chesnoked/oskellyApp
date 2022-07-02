package su.reddot.domain.model.like;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 13.09.17.
 */
@Entity
@Inheritance
@Table(name = "\"like\"")
@Slf4j @Getter @Setter @Accessors(chain = true)
public abstract class Like<T extends Likeable> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;

	public static Like init(Likeable likeable) {
		Like like = null;

		try {
			like = likeable.getLikeClass().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException("Ошибка создания экземпляра класса. Класс: "
					+ likeable.getClass().getName());
		}

		like.setLikable(likeable);

		return like;
	}

	protected abstract void setLikable(T likable);

	public abstract T getLikable();
}
