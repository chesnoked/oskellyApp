package su.reddot.presentation.view;

import lombok.Data;
import lombok.experimental.Accessors;
import su.reddot.domain.model.user.User;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Предлагаю использоват класс для карторчки товара и для публичного профиля
 *
 */

@Data
@Accessors(chain = true)
public class SellerView {

	private static final String registrationTimeFormat = "Присоедини%s к Oskelly %s";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));

	private User originalUser;
	private Long id;

	// FIXME  поля User.firstName не существует
	//private String firstName;
	private String nick;
	// FIXME поля User.country не существует
	private String country = "Россия";

	//FIXME пробросить город(если нет города, как продавца, отдавать город как Покупателя) в  модель USER
	private String city = null;

	/**
	 * ссылка на фотографию
	 */
	private String photo;

	/**
	 * пол
	 */
	private User.Sex sex;

	/**
	 * нужно LDT конвертировать в строку
	 */
	private String registered;

	/**
	 * продано товаров
	 */
	private Integer productsSold;

	/**
	 * подписчиков
	 */
	private Integer subscribers;

	/**
	 * подписок
	 */
	private Integer subscriptions;
	/**
	 * Подписан ли просматривающий пользователь на данного или нет
	 */
	private boolean isFollowed;
	private Integer likes;
	/**
	 * Продавец может быть в статусах PRO, Trusted и т.д.
	 */
	//FIXME сделал только PRO. Остальные пробросить по мере появления
	private boolean proState = false;
	/**
	 * Форматирует LocalDateTime в дату с текстовым месяцем. Устанавливать значение этой переменной только после того,
	 * как был указан пол продавца, иначе обращение будет к мужскому полу
	 *
	 * @param localDateTime
	 */
	public void setRegistered(ZonedDateTime localDateTime) {
		String date = formatter.format(localDateTime);
		String ending = (this.sex != null && this.sex.equals(User.Sex.FEMALE)) ? "лась" : "лся";
		this.registered = String.format(registrationTimeFormat, ending, date);
	}

	public void setIsFollowedContext(boolean followedContext){
		this.isFollowed = followedContext;
	}
	public static SellerView getDefault() {
		SellerView sellerView = new SellerView();
		sellerView.setCountry("Россия");
		sellerView.setNick("Ольга");
		sellerView.setPhoto("/images/tmp/users/userpic-1.jpg");
		sellerView.setSex(User.Sex.FEMALE);
		sellerView.setProductsSold(150);
		sellerView.setSubscribers(100);
		sellerView.setSubscriptions(100);
		sellerView.setLikes(100);
		sellerView.setRegistered(ZonedDateTime.now());
		return sellerView;
	}

}
