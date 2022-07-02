package su.reddot.domain.model.product;

import lombok.Data;
import su.reddot.domain.model.attribute.AttributeValue;

import javax.persistence.*;

@Entity
@Data
public class ProductAttributeValueBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private AttributeValue attributeValue;
}
