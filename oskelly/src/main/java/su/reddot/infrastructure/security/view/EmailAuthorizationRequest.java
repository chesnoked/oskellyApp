package su.reddot.infrastructure.security.view;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class EmailAuthorizationRequest {
	@NotBlank(message = "Не указан email")
	@Email(message = "Неверный формат email")
	private String email;
	@NotBlank(message = "Не указан пароль")
	private String password;
}
