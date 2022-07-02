package su.reddot.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import su.reddot.domain.dao.LocalDateTimeConverter;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Корзина товаров пользователя
 */
@Entity
@Data @NoArgsConstructor
public class ShoppingCart {

    public ShoppingCart(User user, ProductItem productItem) {
        this.user = user;
        this.productItem = productItem;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductItem productItem;

    @Column(insertable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime addTime;
}
