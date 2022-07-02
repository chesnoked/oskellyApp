package su.reddot.domain.model.product;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProductStatusBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private ProductStatus productStatus;
}
