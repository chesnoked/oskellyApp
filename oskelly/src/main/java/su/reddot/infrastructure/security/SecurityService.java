package su.reddot.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.validation.ValidNewPassword;
import su.reddot.infrastructure.security.view.EmailRegistrationRequest;
import su.reddot.infrastructure.util.ErrorNotification;
import su.reddot.presentation.mobile.api.v1.SecurityRestControllerV1;

public interface SecurityService {

	/**
	 * Аутентифицировать пользователя в системе
	 *
	 * @param email             email, под которым пользователь был зарегистрирован
	 * @param password          пароль пользователя
	 * @param errorNotification класс, содержащий ошибки, предназначенные для уведомления пользователя
	 *
	 * ВНИМАНИЕ!!! Править осторожно! Используется в API MOBILE V1
	 * Можно сломать мобильное приложение
	 * @see SecurityRestControllerV1#register(EmailRegistrationRequest, BindingResult)
	 */
	Authentication loginByEmail(String email, String password, ErrorNotification errorNotification);

	void sendAuthorizationConfirmation(User user);

	void activateUserByActivationToken(Long userId, String token) throws SecurityException;

	/** Запросить сброс пароля для учетной записи с указанным почтовым ящиком. */
	void requestForPasswordReset(PasswordResetRequest req);

	/** Задать новый пароль. */
	void updatePassword(NewPasswordRequest req);

	void assertTokenValidity(String possiblyInvalidToken);

	@Getter @Setter
	class PasswordResetRequest {

		@NotBlank(message = "Не указан адрес электронной почты")
		@Email(message = "Неверный формат адреса электронной почты")
		private String email;
	}

	@ValidNewPassword
	@Getter @Setter
	class NewPasswordRequest
	{
	    @NotBlank(message = "Не указан пароль")
		private String password;

		@NotBlank(message = "Не указан пароль")
		private String passwordOnceMore;

		@NotBlank(message = "Не указан токен")
		private String resetToken;
	}
}
