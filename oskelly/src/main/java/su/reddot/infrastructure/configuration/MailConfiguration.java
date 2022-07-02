package su.reddot.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import su.reddot.infrastructure.sender.NotificationMailConfiguration;
import su.reddot.infrastructure.sender.RegistrationMailConfiguration;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties({
        NotificationMailConfiguration.class,
        RegistrationMailConfiguration.class})
@RequiredArgsConstructor
public class MailConfiguration {

    /**
     * Клиент для отправки сообщения с токеном подтверждения регистрации.
     * На боевом и тестовом серверах проверять подключение к почтовому серверу
     **/
    @Profile("!debug")
    @Bean
    public JavaMailSender registrationStuffSender(RegistrationMailConfiguration c)
            throws MessagingException {

        JavaMailSenderImpl sender = template(c.getHost(), c.getPort(), c.getUsername(), c.getPassword());

        sender.testConnection();

        return sender;
    }

    @Profile("debug")
    @Bean @Qualifier("registrationStuffSender")
    public JavaMailSender debugRegistrationStuffSender(RegistrationMailConfiguration c)
            throws MessagingException, UnsupportedEncodingException {

        return template(c.getHost(), c.getPort(), c.getUsername(), c.getPassword());
    }

    /**
     * Клиент для отправки нотификаций по почте.
     * На боевом и тестовом серверах проверять подключение к почтовому серверу
     * перед запуской приложения.
     **/
    @Profile("!debug")
    @Bean
    public JavaMailSender notificationSender(NotificationMailConfiguration c) throws MessagingException {

        JavaMailSenderImpl sender = template(
                c.getHost(), c.getPort(),
                c.getUsername(), c.getPassword());

        sender.testConnection();

        return sender;
    }

    @Profile("debug")
    @Bean @Qualifier("notificationSender")
    public JavaMailSender debugNotificationSender(NotificationMailConfiguration c)
            throws MessagingException, UnsupportedEncodingException {

        return template(c.getHost(), c.getPort(), c.getUsername(), c.getPassword());
    }

    private JavaMailSenderImpl template(String host, int port, String username, String pwd) {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(pwd);

        /*
        * Настройки javamail сессии.
        * Перечень доступных настроек:
        * https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html;
        *
        * Скопировано с настроек уже существующего клиента:
        * registration at oskelly dot ru
        **/
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.quitwait", true);

        return sender;
    }
}