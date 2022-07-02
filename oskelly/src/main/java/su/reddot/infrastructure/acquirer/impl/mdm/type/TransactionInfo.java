package su.reddot.infrastructure.acquirer.impl.mdm.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
public class TransactionInfo {

    /** ID транзакции в системе Банка */
    private int id;

    /** Тип операции  */
    private TransactionType type;

    /** Уникальный ID покупки в магазине */
    private String orderId;

    /** Код терминала */
    private String terminalId;

    /** Токен операции */
    private String token;

    /** Дата и время запроса в ISO формате yyyy-MM-ddTHH:mm:sszzz */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private ZonedDateTime requestDate;

    /** Сумма и валюта запроса. */
    private Money amount;

    /** Описание платежа */
    private String description;

    /** Информация о карте */
    private Card sourceCard;

    /** Информация о карте получателя для card_to_card и business_to_card переводов */
    private Card destinationCard;

    /** Информация о клиенте */
    private Customer customer;

    /** Информация для добавления к данным платежа */
    private String additionalInfo;


    /** Информация о статусе транзакции */
    private Status status;

    /**  Информация об авторизации */
    private TransactionRefSet refSet;

    /** Дата и время транзакции в ISO формате yyyy-MM-ddTHH:mm:sszzz */
    private String transDate;

    /** Дата расчета в формате  yyyy-MM-dd */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate postingDate;

    /** ID оригинальной транзакции для отмен refund и завершения расчета hold_completion */
    private int originalTransactionId;

    /** URL мерчанта для отправки статуса операции */
    private String callbackUrl;

    /** URL магазина для возврата после завершения платежа */
    private String returnUrl;

    /**  Опция генерации токена по карте. */
    private CardTokenRequestType request_card_token;

    /** Опция повторной операции (рекурент), проходящей без участия клиента */
    private boolean recurring;

    /** Подпись запроса (используется для режима callback)*/
    private String signature;

    /* TODO скорее всего не понадобится
    private Addendum addendum;
    */

    public String getSequenceToSign() {
        String template = "id=%dorder_id=%sterminal_id=%stoken=%srequest_date=%samount.value=%.2famount.currency=%s"
                + "status.type=%s";
        String sequenceToSign = String.format(Locale.ROOT, template, id, orderId, terminalId, token,
                requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")), amount.getValue(), amount.getCurrency(), status.getType());

        /* Поля refSet может и не быть в сообщении. Если его нет, то по спецификации в выработке подписи оно не участвует. */
        if (refSet != null) {
            sequenceToSign += String.format("ref_set.auth_code=%sref_set.ret_ref_number=%s",
                    refSet.getAuthCode(), refSet.getRetRefNumber());
        }

        return sequenceToSign;
    }

}
