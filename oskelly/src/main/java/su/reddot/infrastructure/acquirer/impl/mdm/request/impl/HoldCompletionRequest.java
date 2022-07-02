package su.reddot.infrastructure.acquirer.impl.mdm.request.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import su.reddot.infrastructure.acquirer.impl.mdm.request.Request;
import su.reddot.infrastructure.acquirer.impl.mdm.type.Money;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Запрос завершения платежа
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HoldCompletionRequest implements Request {


    /**
     * Токен ключа продавца (предоставляется при регистрации)
     */
    private String token;

    /**
     * ID транзакции в системе Банка для завершения расчета
     */
    private int originalTransactionId;

    /**
     * Уникальный ID покупки в магазине.
     * Уникальность order_id проверяется для транзакций со статусом, отличным от
     * error. То есть, если транзакция завершилась ошибкой, допускается повторная
     * транзакция с таким же order_id
     */
    private Long orderId;


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

    public HoldCompletionRequest(String token, int originalTransactionId,  Long orderId,
                                 ZonedDateTime requestDateTime, BigDecimal amountValue) {

        this.token = token;
        this.originalTransactionId = originalTransactionId;
        this.orderId = orderId;
        this.requestDate = requestDateTime.truncatedTo(ChronoUnit.SECONDS);
        this.amount = new Money(amountValue);
    }

    @Override
    @JsonIgnore
    public String getSequenceToSign() {
        String sequenceTemplate = "token=%soriginal_transaction_id=%sorder_id=%srequest_date=%samount.value=%samount.currency=%s";
        return String.format(sequenceTemplate,
                token,
                originalTransactionId,
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
