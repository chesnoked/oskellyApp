package su.reddot.infrastructure.acquirer.impl.mdm.type;

/** Типы платежных систем */
enum PaymentSystem {
    visa,
    master_card,
    amex, // American Express
    mir,

    /* такое значение платежной системы наблюдается при получении ответа
      от банка на ранее совершенный тестовый платеж */
    unknown
}
