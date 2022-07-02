package su.reddot.infrastructure.acquirer.impl.mdm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки, необходимые для формирования платежных запросов на MdmPay
 */
@ConfigurationProperties(prefix = "acquirer.mdm-pay")
@Getter @Setter
public class MdmPayConfiguration {
    private String token;

    private String callbackUrl;

    private String returnUrl;

    private String merchantName;

    private String signingKey;

    private String apiEndpoint;
}
