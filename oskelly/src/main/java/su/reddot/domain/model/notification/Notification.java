package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Vitaliy Khludeev on 18.09.17.
 */
@Entity
@Inheritance
@Getter
@Setter
@Accessors(chain = true)
public abstract class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;

	/**
	 * Дата прочтения может устанавливаться и через SQL UPDATE
	 * @see su.reddot.domain.service.notification.NotificationService#readAll(User)
	 */
	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime readTime;

	@ManyToOne
	protected User user;

	public boolean isRead() {
		return readTime != null;
	}

	public String formattedCreateTime(TimeZone nullableTimeZone){
		Optional<TimeZone> timeZoneOptional = Optional.ofNullable(nullableTimeZone);
		return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(getCreateTime().withZoneSameInstant(timeZoneOptional.orElse(TimeZone.getDefault()).toZoneId()));
	}

	public String getFullMessageText() {
		 return getInitiator().map(u -> u.getNickname() + " ").orElse("") + getBaseMessage() + getTargetUser().map(u -> " " + u.getNickname()).orElse("");
	}

	/**
	 * Пользователь, который иницировал событие
	 * @return инициатор
	 */
	public abstract Optional<User> getInitiator();

	/**
	 * Пользователь, в отношении которого было совершено действие
	 */
	public abstract Optional<User> getTargetUser();

	/**
	 * Изображение объекта, в отношении которого было совершено действие
	 * Например, изображение опубликованного товара
	 * @return путь к изображению в файловой системе
	 */
	public abstract Optional<String> getImageOfTargetObject();

	/**
	 * Изображение объекта, которое должно отдаваться если Notification#getImageOfTargetObject пустое
	 * @see Notification#getImageOfTargetObject пустое
	 * @return путь к изображению целиком, а не в файловой системе
	 */
	public abstract String getEmptyImageOfTargetObject();

	/**
	 * URL, ведущий на страницу объекта, в отношении которого было совершено действие
	 * Например, на страницу товара или страницу подтверждения продажи
	 */
	public abstract String getUrlOfTargetObject();

	/**
	 * Базовое сообщение нотификации, например в сообщении "user1 подписался(ась) на новости user2"
	 * текст "подписался(ась) на новости" будет базовым сообщением
	 */
	public abstract String getBaseMessage();

	public abstract NotificationType getType();

	public enum NotificationType {
		NEWS,
		NOTIFICATION
	}
}