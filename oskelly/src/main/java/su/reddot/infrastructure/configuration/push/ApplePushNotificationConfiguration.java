package su.reddot.infrastructure.configuration.push;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
@ConfigurationProperties(prefix = "app.push.apple")
@Getter @Setter
public class ApplePushNotificationConfiguration {

	private String certificatePath;

	private String certificatePassword;
}
