package su.reddot.infrastructure.sender;

public interface NotificationSender {

    void send(String to, String subject, String text);

    void sendViaRegistrationBox(String to, String subject, String text);
}
