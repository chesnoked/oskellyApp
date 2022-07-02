package su.reddot.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author sergeykultishev
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class PriceUpdateSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private User subscriber;

    @ManyToOne
    @NotNull
    private Product product;
}
