package su.reddot.infrastructure.acquirer.impl.mdm.type;

enum TransactionType {

    /** Платеж */
    payment,

    /** Авторизация (блокировка средств под платеж) */
    hold,

    /** Завершение авторизации (расчета) */
    hold_completion,

    /** Отмена\Возврат */
    refund,

    /** Перевод с карты на карту */
    card_to_card,

    /** Перевод со счета на карту */
     business_to_card,

    /** Перевод с карты на счет */
     card_to_business
}
