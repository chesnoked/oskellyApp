package su.reddot.infrastructure.acquirer.impl.mdm.request.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import su.reddot.infrastructure.acquirer.impl.mdm.request.Request;
import su.reddot.infrastructure.acquirer.impl.mdm.type.Money;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionInfo;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Запрос оплаты заказа.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FinancialRequest implements Request {

    /**
     * Токен ключа продавца (предоставляется при регистрации)
     */
    private String token;

    /**
     * URL магазина для отправки статуса операции {@link TransactionInfo}
     */
    private String callbackUrl;

    /**
     * URL магазина для возврата после завершения платежа
     */
    private String returnUrl;

    /**
     * Название магазина для отображения на странице
     */
    private String merchantName;

    /**
     * Уникальный ID покупки в магазине. Может быть произвольной строкой.
     * Уникальность order_id проверяется для транзакций со статусом, отличным от
     * error. То есть, если транзакция завершилась ошибкой, допускается повторная
     * транзакция с таким же order_id
     */
    private String orderId;


    /* Значение подписи запроса в 0x формате */
    private String signature;

    /**
     * Сумма и валюта запроса.
     */
    private Money amount;

    /**
     * Дата и время запроса в ISO формате yyyy-MM-ddTHH:mm:sszzz
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private ZonedDateTime requestDate;

    public FinancialRequest(BigDecimal amountValue, String orderId, ZonedDateTime requestDateTime,
                     String merchantName, String token,
                     String callbackUrl, String returnUrl) {

        this.token = token;
        this.callbackUrl = callbackUrl;
        this.returnUrl = returnUrl;
        this.merchantName = merchantName;
        this.orderId = orderId;
        this.requestDate = requestDateTime.truncatedTo(ChronoUnit.SECONDS);
        this.amount = new Money(amountValue);
    }

    /** Получить строку, с использованием которой вычисляется подпись сообщения */
    @Override
    public String getSequenceToSign() {
        String sequenceTemplate = "token=%sorder_id=%srequest_date=%samount.value=%samount.currency=%s";
        return String.format(sequenceTemplate,
                token,
                orderId,
                /* Явное форматирование времени с часовым поясом в виде ЧЧ:MM.
                 * Если часовой пояс - UTC (нулевой пояс),
                 * то стандартный формат ISO_OFFSET_DATE_TIME представит часовой пояс не в виде +00:00,
                 * а в виде литерала Z. Например, 2000-01-01T00:00:00Z.
                 * MdmPay же, получив поле requestDate запроса, представляет его временную зону в виде часа и времени,
                 * разделенных двоеточием: UTC зона будет выглядеть как +00:00. Именно от этого строкового
                 * представления mdm pay вычислит подпись запроса.
                 */
                requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")),
                amount.getValue(),
                amount.getCurrency());
    }
}
