package su.reddot.infrastructure.acquirer.impl.mdm.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Информация об авторизации */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
class TransactionRefSet {

    /** Код авторизации */
    private String authCode;

    /** Код RRN = Retrieval Reference Number */
    private String retRefNumber;

    /** Код ARN = Acquirer Reference Number */
    private String acqRefNumber;
}
