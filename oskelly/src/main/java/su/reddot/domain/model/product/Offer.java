package su.reddot.domain.model.product;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static javax.persistence.FetchType.LAZY;

/**
 * Предложение со стороны покупателя о снижении цены на конкретный товар.
 */
@Entity
@Getter @Setter @Accessors(chain = true)
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Товар, для которого покупатель предлагает снизить цену. */
    @ManyToOne(fetch = LAZY)
    private Product product;

    /** Новая цена, которую предложил покупатель. */
    private BigDecimal price;

    /** Покупатель, который предлагает новую цену на товар. */
    @ManyToOne(fetch = LAZY)
    private User offeror;

    /** Реакция продавца на запрос снижения цены.
     * true - продавец подтвердил предложение,
     * false - отказался от предложения,
     * null - еще не вынес решения. */
    private Boolean isAccepted;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;
}
