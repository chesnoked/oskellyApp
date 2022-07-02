package su.reddot.infrastructure.acquirer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Сущность внешнего сервиса, предоставляющего услуги
 * проведения платежных операций через интернет ( интернет - эквайринга )
 */
public interface Acquirer {
    /**
     * Содержимое запроса на оплату
     * @param p оплачиваемый заказ
     * @param returnUrlPath путь (без домена), по которому покупатель может перейти со страницы платежного шлюза обратно в магазин
     * @param requestDateTime время запроса
     * @return запрос на оплату
     */
    String getPaymentRequest(Payable p, String urlToReturnFromPaymentPage, ZonedDateTime requestDateTime);

    /**
     * Обработать входящую транзакцию
     * @param transactionInfo данные транзакции
     */
    void handleTransactionInfo(String transactionInfo);

    /**
     * Зарегистрировать компонент, которому нужно получать сообщения от платежного шлюза
     * по результатам платежных операций.
     */
    void registerTransactionHandler(TransactionHandler h);

    /**
     * Завершить расчет платежа после операции удержания средств на счету клиента (hold)
     * @param transactionId идентификатор оригинальной hold транзакции
     * @param orderId идентификатор заказа, который был указан в оригинальной hold транзакции
     * @param holdAmount итоговая сумма, которая будет списана со счета клиента
     */
    void completeHold(int transactionId, Long orderId, BigDecimal holdAmount, ZonedDateTime requestDateTime);

    /**
     * Отменить оригинальную транзакцию платежа.
     * @param transactionId идентификатор оригинальной транзакции платежа
     * @param orderId идентификатор заказа, который был указан в оригинальном платеже
     * @param refundAmount сумму, которую нужно вернуть клиенту
     * @param requestDateTime дата и время запроса
     */
    void refund(int transactionId, Long orderId, BigDecimal refundAmount, ZonedDateTime requestDateTime);
}

