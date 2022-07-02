package su.reddot.infrastructure.cashregister.impl.starrys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для взаимодействия с сервисом облачных фискальных регистраторов
 * @see <a href="http://check.starrys.ru/">http://check.starrys.ru/</a>
 */
@ConfigurationProperties(prefix = "cash-register.starrys")
@Getter @Setter
public class StarrysConfiguration {

    private String clientId;

    private int password;

    /** Способ расчета (предварительная полная оплата, предв. частичная, аванс, полная оплата и тд.) */
    private short payAttribute;

    /** тип системы налогообложения (общая, упрощенная доход, упрощенная доход минус расход, ЕНВД и тд.) */
    private short taxId;

    private String place;

    /** получать ли полный ответ (false для сокращенной версии ответа) */
    private boolean fullResponse;

    private String endpoint;
    private String clientCertificatePath;
    private String clientCertificatePassword;
}
