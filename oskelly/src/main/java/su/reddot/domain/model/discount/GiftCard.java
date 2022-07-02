package su.reddot.domain.model.discount;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.acquirer.Payable;
import su.reddot.infrastructure.cashregister.Checkable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/** Подарочный сертификат */
@Entity
@AttributeOverrides({
        @AttributeOverride(name = "recipient.name", column = @Column(name = "recipient_name")),
        @AttributeOverride(name = "recipient.email", column = @Column(name = "recipient_email")),
        @AttributeOverride(name = "recipient.address", column = @Column(name = "recipient_address"))
})
@Getter @Setter @Accessors(chain = true)
public class GiftCard implements Payable, Checkable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Стоимость, которую оплачивает покупатель сертификата,
     * и одновременно - сумма скидки, которую дает сертификат. */
    private BigDecimal amount;

    /** Уникальный код сертификата; устанавливается после успешной покупки сертификата. */
    private String code;

    /** Пользователь, который купил этот сертификат */
    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    /** Заказ, в котором применили этот сертификат */
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    /** Имя пользователя, дарящего этот сертификат */
    private String givingName;

    /** Пользователь, которому дарят этот сертификат */
    @Embedded
    private Recipient recipient;

    /** Текущее состояние сертификата */
    @Enumerated(EnumType.STRING)
    private State state;
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime stateTime;

    /** Чек оплаты этого сертификата */
    private String buyerCheck;

    /** Срок истечения действия сертификата. */
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime expiresAt;

    public boolean isPayable() { return state == State.CREATED; }

    @Override
    public String getOrderId() {
        return String.format("1-%s", id);
    }

    @Override
    public BigDecimal getPaymentAmount() {
        return amount.setScale(2, RoundingMode.UP);
    }

    public GiftCard setState(State s) {
        state = s;
        stateTime = ZonedDateTime.now();

        if (s == State.PAYED) { expiresAt = ZonedDateTime.now().plusMonths(6); }

        return this;
    }

    public boolean isExpired() {
        return expiresAt != null
                && ZonedDateTime.now().compareTo(expiresAt) > 0;
    }

    public BigDecimal getPriceWithDiscount(BigDecimal originalPrice) {
        BigDecimal priceWithDiscount = originalPrice.subtract(amount);

        return priceWithDiscount.signum() > -1? priceWithDiscount : BigDecimal.ZERO;
    }

    @Override
    public String getRequestId() { return String.format("1-%s", id); }

    @Override
    public BigDecimal getNonCashAmount() { return amount; }

    @Override
    public List<Line> getLines() {
        return Collections.singletonList(
            new Line(1, amount, "Подарочный сертификат")
        );
    }

    /** Пользователь, которому дарят сертификат */
    @Embeddable
    @Getter @Setter @NoArgsConstructor
    public static class Recipient {

        private String name;

        /** Адрес доставки физической копии сертификата.
         * Задается только если сертификат нужно выслать в виде физической копии. */
        private String address;

        /** Почтоый адрес, на который нужно выслать электронную копию сертификата.
         * Задается только если сертификат нужно выслать в электронном виде. */
        private String email;
    }

    /** Состояния, в которых может находиться сертификат (по аналогии с состояниями заказа)
     *
     * @see su.reddot.domain.model.order.OrderState
     */
    public enum State {

        /** Покупатель успешно создал сертификат, правильно заполнив все необходимые данные,
         * но пока не перешел к его оплате. */
        CREATED,

        /** Покупатель перешел на оплату, но платежный шлюз (ПШ) еще не прислал ответ об успешной оплате. */
        PAYMENT_STARTED,

        /** Покупатель успешно оплатил сертификат, и ПШ уведомил нас об этом. */
        PAYED,

        /** ПШ сообщил о том, что покупатель не смог оплатить сертификат.
         * В этом случае покупатель может выполнить платеж повторно. */
        PAYMENT_FAILED
    }
}
