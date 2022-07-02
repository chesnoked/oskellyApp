package su.reddot.infrastructure.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подключения к аккаунту, от имени которого отправляется письмо подтверждения регистрации.
 */
@ConfigurationProperties(prefix = "app.mail.registration")
@Getter @Setter
public class RegistrationMailConfiguration {

    private String host;

    private int port;

    private String username;

    private String password;

    private String personal;
}
