package su.reddot.infrastructure.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationSender implements NotificationSender {

    private       JavaMailSender                sender;
    private final NotificationMailConfiguration conf;

    private       JavaMailSender                registrationStuffSender;
    private final RegistrationMailConfiguration registrationStuffConf;

    @Autowired
    public void setSender(@Qualifier("notificationSender") JavaMailSender s) {
        sender = s;
    }

    @Autowired
    public void setRegistrationStuffSender(@Qualifier("registrationStuffSender") JavaMailSender s) {
        registrationStuffSender = s;
    }

    @Override @Async
    public void send(String to, String subject, String text) {

        send(sender, conf.getUsername(), conf.getPersonal(), to, subject, text);

    }

    @Override @Async
    public void sendViaRegistrationBox(String to, String subject, String text) {

            send(registrationStuffSender, registrationStuffConf.getUsername(),
                    registrationStuffConf.getPersonal(), to, subject, text);
    }

    private void send(JavaMailSender sender,
                      String from, String personal,
                      String to, String subject, String text) {

        MimeMessage       message = sender.createMimeMessage();
        MimeMessageHelper helper  = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setSubject(subject);
            helper.setFrom(from, personal);
            helper.setTo(to);
            helper.setText(text, true);
            sender.send(message);
        } catch (Exception e) {
            log.error("Ошибка отправки почтового сообщения. От: {}, кому: {}, тема: {}, сообщение:{}",
                    from, to, subject, text, e);
        }
    }

}
