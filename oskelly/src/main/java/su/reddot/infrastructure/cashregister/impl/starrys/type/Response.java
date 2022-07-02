package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Результат выполнения отдельной команды. */
@Getter @Setter @JsonNaming(CustomNamingStrategy.class)
class Response {

    /** Тип фискального документа. */
    private byte fiscalDocumentType;

    /** Номер фискального документа.
     * @apiNote в документации имеет тип uint32, который в общем случае не помещается в int,
     * поэтому для его представления используется long.
     **/
    private long fiscalDocNumber;

    /** Подпись фискального документа.
     * @apiNote в документации имеет тип uint32, который в общем случае не помещается в int,
     * поэтому для его представления используется long.
     **/
    private long fiscalSign;

    /** Номер смены (для отчетов об открытии/закрытии смены).
     * @apiNote в документации имеет тип uint16, который в общем случае не помещается в short,
     * поэтому для его представления используется int.
     **/
    private int turnNumber;

    /** Код ошибки */
    private short error;

    /** Список сообщений, сформированных устройством при обработке запроса */
    private List<String> errorMessage;

    private Tag fiscalDocument;
}
