package su.reddot.infrastructure.esputink;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class EmailSubscriptionRequest {
    @NotBlank(message = "Не указан email")
    @Email(message = "Неверный формат email")
    private String email;
    @NotBlank(message = "Введите имя")
    private String name;
}
