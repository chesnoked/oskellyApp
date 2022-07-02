package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor @Getter @Setter @Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyerCheckRequest {

    /**
     * Должно иметь значение auto (цитата из документации)
     */
    private final String device = "auto";

    /**
     * Уникальный идентификатор запроса
     */
    private final String requestId;

    /**
     * Пароль команды. Пароли по умолчанию имеют значения от 1 до 30.
     */
    private final int password;

    private final String clientId;

    /**
     * Тип чека (на текущем этапе - всегда "приход")
     */
    private final short documentType = 0;

    /**
     * Массив товарных позиций
     */
    private final List<Line> lines;

    private Long cash;

    private final List<Long> nonCash;

    private Long advancePayment;

    private Long credit;

    private Long consideration;

    private Short taxMode;

    private String phoneOrEmail;

    private String place;

    private Short maxDocumentsInTurn;

    private boolean fullResponse;
}

