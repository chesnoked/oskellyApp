package su.reddot.domain.model;

import lombok.Data;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.*;

@Entity
@Data
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //TODO повесить ограничение (Пользователь, Продукт) - уникальное сочетание
    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;
}
