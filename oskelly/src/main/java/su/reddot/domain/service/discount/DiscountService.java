package su.reddot.domain.service.discount;

import su.reddot.domain.model.discount.GiftCard;
import su.reddot.domain.model.discount.PromoCode;
import su.reddot.domain.model.user.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface DiscountService {

    /** Найти промо-код по его коду*/
    Optional<PromoCode> findPromoCode(String code);

    /** Найти подарочный сертификат по его коду*/
    Optional<GiftCard> findGiftCard(String code);

    GiftCard createGiftCard(BigDecimal amount, String givingName, GiftCard.Recipient recipient, User buyer);

    /** Получить запрос на оплату сертификата. */
    String getPaymentRequest(Long giftCardId, User u);
}
