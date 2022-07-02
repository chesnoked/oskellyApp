package su.reddot.domain.service.attractionOfTraffic;

import su.reddot.domain.model.user.User;

import javax.servlet.http.Cookie;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 10.10.17.
 */
public interface AttractionOfTrafficService {

	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@SuppressWarnings("unused") /* вызывается неявно при появлении соответствующего события */
	void sendAttractionOfTrafficResult(AttractionOfTrafficEvent e);

	void saveIfNeed(User user, List<Cookie> cookies);

	/** При регистрации пользователя отправлять запрос на advertise */
	void track(User u, List<Cookie> c);
}
