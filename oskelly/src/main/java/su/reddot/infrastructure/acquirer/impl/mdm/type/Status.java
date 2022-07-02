package su.reddot.infrastructure.acquirer.impl.mdm.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/** Информация о статусе операции */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter
public class Status {

    /** Статус операции */
    private TransactionStatus type;

    /** Код ошибки */
    private String errorCode;

    /** Текстовое описание ошибки */
    private String errorDescription;

    /** ID транзакции, связанной с ошибкой */
    private int transactionId;

}
