package su.reddot.infrastructure.acquirer.impl.mdm.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.YearMonth;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
class Card {

    /** Номер карты PAN */
    private String number;

    /** Срок действия */
    private YearMonth expiryDate;

    private String cvc2;

    /** Токен карты
     * Опционально формируется в ответе платежа или перевода и используется в дальнейшем
     * вместо номера карты и срока действия. Пример
     * CARD:61:E0:72:04:58:4F:4E:21:B6:81:29:26:F1:91:C6:B9
     */
    private String token;

    /** Держатель карты */
    private String holder;

    /** Маскированный номер. Например, 5543XXXXXXXX6654 */
    private String maskedNumber;

    private PaymentSystem paymentSystem;

    /** Цифровой код страны ISO 3166-1 */
    private String countryCode;

    /** Банк-эмитент */
    private String bankName;
}
