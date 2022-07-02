package su.reddot.domain.service.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.view.EmailRegistrationRequest;
import su.reddot.infrastructure.security.view.EmailValidationRequest;
import su.reddot.infrastructure.util.ErrorNotification;

import java.util.Optional;

public interface UserService {

	/**
	 * Регистрируем пользователя в системе
	 *
	 * @param request      класс с параметрами запроса. Класс перед тем, как попасть в сервис, прошел валидацию через
	 *                     spring validation api и не имеет null или blank полей.
	 * @param notification класс, содержащий ошибки, предназначенные для уведомления пользователя
	 * @return Optional.empty в случае, если в процессе создания пользователя возникли ошибки
	 */
	Optional<User> registerUserByEmail(EmailRegistrationRequest request, ErrorNotification notification);

	/**
	 * Проверяем email на то, что он не был ранее зарегистрирован
	 * @return
	 */
	void checkEmail(EmailValidationRequest request, ErrorNotification notification);

    User getUserByEmail(String email);

	Optional<User> getUserById(Long id);

	Optional<User> getUserByNickname(String nickname);

	void setDeliveryRequisite(Long userId, DeliveryRequisite r);

	void save(User u);

	UserWithPassword registerSilently(String name, String nickname, String email);

	@Getter @Setter @Accessors(chain = true)
	class UserWithPassword {
		private User user; private String password;
	}
}
