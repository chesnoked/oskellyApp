package su.reddot.infrastructure.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подключения к аккаунту, от имени которого отправляются уведомления.
 */
@ConfigurationProperties(prefix = "app.mail.notification")
@Getter @Setter
public class NotificationMailConfiguration {

    private String host;

    private int port;

    private String username;

    private String password;

    private String personal;
}
