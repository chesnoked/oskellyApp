package su.reddot.domain.service.discount.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.discount.GiftCardRepository;
import su.reddot.domain.dao.discount.PromoCodeRepository;
import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.discount.DiscountService;
import su.reddot.infrastructure.acquirer.Acquirer;
import su.reddot.infrastructure.acquirer.TransactionHandler;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionInfo;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionStatus;
import su.reddot.infrastructure.cashregister.CashRegister;
import su.reddot.infrastructure.sender.NotificationSender;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultDiscountService implements DiscountService, TransactionHandler {

    private final PromoCodeRepository promoCodeRepository;
    private final GiftCardRepository  giftCardRepository;

    private final NotificationSender sender;

    private final TemplateEngine templateEngine;
    private final Acquirer     acquirer;
    private final CashRegister cashRegister;

    @PostConstruct /* Как вариант, использовать вместо этого spring events*/
    public void registerItselfAsTransactionHandler() {
        acquirer.registerTransactionHandler(this);
    }

    @Override
    public Optional<PromoCode> findPromoCode(String code) {
        return promoCodeRepository.findByCode(code);
    }

    @Override
    public Optional<GiftCard> findGiftCard(String code) {
        return giftCardRepository.findByCode(code);
    }

    @Override
    public GiftCard createGiftCard(BigDecimal amount, String givingName, GiftCard.Recipient recipient, User buyer) {
        GiftCard giftCardToCreate = new GiftCard()
                .setAmount(amount)
                .setBuyer(buyer)
                .setGivingName(givingName)
                .setState(GiftCard.State.CREATED)
                .setRecipient(recipient);

        return giftCardRepository.save(giftCardToCreate);
    }

    /**
     * Получить запрос на оплату.
     * Успешно выполнится только для сертификата, который еще не оплачен.
     *
     * @param buyer пользователь, который пытается купить сертификат
     * @return запрос на оплату сертификата в виде base64 строки
     * @throws IllegalArgumentException если пользователь пытается оплатить несуществующий сертификат
     * либо сертификат, который ему не принадлежит; или если пользователь пытается оплатить свой сертификат, который уже оплачен.
     */
    @Override
    public String getPaymentRequest(Long giftCardId, User buyer) {

        GiftCard giftCardToPayFor = giftCardRepository.findByIdAndBuyer(giftCardId, buyer)
                 .orElseThrow(() -> new IllegalArgumentException("Сертификат с иденификатором: " + giftCardId + " не найден"));

        if (!giftCardToPayFor.isPayable()) {
            throw new IllegalArgumentException("Сертификат с идентификатором: " + giftCardId + " оплатить нельзя");
        }

        String paymentRequest = acquirer.getPaymentRequest(giftCardToPayFor,

               /* пока нет отдельной страницы для созданного сертификата, возвращаться на главную страницу после его покупки */
                null,

                ZonedDateTime.now());

        giftCardToPayFor.setState(GiftCard.State.PAYMENT_STARTED);
        giftCardRepository.save(giftCardToPayFor);

        return paymentRequest;
    }

    /**
     * Проверить, является ли транзакция ответом на запрос оплаты сертификата.
     * Если является, обработать ее.
     */
    @Override
    public void handleTransaction(TransactionInfo t) {
        String orderId = t.getOrderId();

        boolean orderIdLooksLikeGiftCardOrderId = orderId.startsWith("1-");
        if (!orderIdLooksLikeGiftCardOrderId) { return; }

        Long payedGiftCardId = Long.valueOf(orderId.replaceFirst("1-", ""));

        GiftCard payedGiftCard = giftCardRepository.findOne(payedGiftCardId);
		if (payedGiftCard == null) {
			log.error("Получена ответная транзакция с идентификатором: {}, " +
                            "на запрос оплаты подарочного сертификата с идентификатором: {}, " +
                            "который не найден в системе.",
					orderId, payedGiftCardId);
			return;
		}

		TransactionStatus tranStatus = t.getStatus().getType();

		if (tranStatus == TransactionStatus.success) {
		    payedGiftCard.setState(GiftCard.State.PAYED);

		    boolean codeGenerated = false;
            for (int attempt = 0; attempt < 3 && !codeGenerated; attempt++ ) {
                String giftCardCode = randomString();

                boolean anotherGiftCardExistsWithSameCode
                        = giftCardRepository.existsByCode(giftCardCode);
                if (!anotherGiftCardExistsWithSameCode) {
                    codeGenerated = true;
                    payedGiftCard.setCode(giftCardCode);
                }
            }

            if (codeGenerated) {
                String recipientEmailIfAny = payedGiftCard.getRecipient().getEmail();
                if (recipientEmailIfAny != null) {

                    Context context = new Context();
                    context.setVariable("code", payedGiftCard.getCode());
                    context.setVariable("value", formatDiscountValue(payedGiftCard.getAmount()));

                    String emailContent = templateEngine.process("mail/gift-card", context);

                    sender.send(recipientEmailIfAny, "Подарочный сертификат", emailContent);
                }
            }
            else {
                log.error("Не удалось сгенерировать код для оплаченного подарочного сертификата " +
                        "с идентификатором: {}", payedGiftCardId);
            }

            /* Неудачный запрос получения чека не должен помешать успешной обработке оплаты сертификата.
            * Запросить чек можно будет повторно в следующий раз (через админку например, или фоновым заданием). */
            try {
                String buyerCheck = cashRegister.requestCheck(payedGiftCard);
                payedGiftCard.setBuyerCheck(buyerCheck);
            }
            catch (Exception e) {
                log.error("Ошибка получения чека покупки подарочного сертификата. " +
                        "Идентификатор сертификата: {}", payedGiftCard.getId(), e);
            }

            giftCardRepository.save(payedGiftCard);
        }
		else if (tranStatus == TransactionStatus.error) {
            payedGiftCard.setState(GiftCard.State.PAYMENT_FAILED);
            giftCardRepository.save(payedGiftCard);
		}
		else {
			// TODO обрабатывать все типы транзкаций
			log.error("Транзакция, которая получена от банка, не обработана: нет обработчика для такого типа транзакции. " +
					"Содержимое транзакции: {}", t.toString());
		}
    }

    private final Random rand = new Random();
    private String randomString() {

        String randomStringAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            char c = randomStringAlphabet.charAt(rand.nextInt(randomStringAlphabet.length()));
            sb.append(c);
        }

        return sb.toString();
    }

    private String formatDiscountValue(BigDecimal value) {

        /* Разделять тысячи пробелами */
        DecimalFormat        f  = new DecimalFormat("###,###,###");
        DecimalFormatSymbols s = new DecimalFormatSymbols();

        s.setGroupingSeparator(' ');
        f.setDecimalFormatSymbols(s);

        return f.format(value);
    }
}
