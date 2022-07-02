package su.reddot.infrastructure.acquirer.impl.mdm.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {

    created("Начальный статус"),

    success("Успех"),

    hold_wait("Деньги успешно заблокированы"),

    processing("Платеж обрабатывается"),

    to_be_confirmed("Платеж на стадии 3DS / требует подтверждения "),

    error("Ошибка"),

    reserved("Операция отменена"),

    partial_reserved("Операция отменена частично ");

    private final String description;
}
