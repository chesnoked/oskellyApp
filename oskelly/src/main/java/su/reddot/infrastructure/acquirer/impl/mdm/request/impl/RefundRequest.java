package su.reddot.infrastructure.acquirer.impl.mdm.request.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import su.reddot.infrastructure.acquirer.impl.mdm.request.Request;
import su.reddot.infrastructure.acquirer.impl.mdm.type.Money;
import su.reddot.infrastructure.acquirer.impl.mdm.type.Status;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Запрос отмены оригинальной транзакции.
 * Все поля за исключением {@code sequenceNumber} совпадают с {@link HoldCompletionRequest}
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RefundRequest implements Request {

    private String token;

    private int originalTransactionId;

    private Long orderId;

    /**
     * Уникальный номер операции отмены.
     * В случае обнаружения повторной транзакции с другой суммой система вернет ошибку
     * дублирования (код 1011) с указанием ID транзакции отмены ({@link Status})
     * В случае повторной транзакции с такой же суммой система вернет информацию по проведенной ранее транзакции.
     */
    private String sequenceNumber = "564";

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private ZonedDateTime requestDate;

    private Money amount;

    private String signature;

    public RefundRequest(String token, int originalTransactionId,  Long orderId,
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
        String sequenceTemplate = "token=%soriginal_transaction_id=%sorder_id=%ssequence_number=%srequest_date=%samount.value=%samount.currency=%s";
        return String.format(sequenceTemplate,
                token,
                originalTransactionId,
                orderId,
                sequenceNumber,
                requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")),
                amount.getValue(),
                amount.getCurrency());
    }
}
