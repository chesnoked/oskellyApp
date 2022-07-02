package su.reddot.infrastructure.security.view;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
public class EmailRegistrationRequest {
	@NotBlank(message = "Не указан email")
	@Email(message = "Неверный формат email")
	private String email;
	@NotBlank(message = "Не указан пароль")
	@Size(min = 3, message = "Слишком короткий пароль")
	private String password;
	@NotBlank(message = "Не указан пароль")
	private String confirmPassword;
	private String phone;
	@NotBlank(message = "Не указан псевдоним")
	private String nickname;
}
