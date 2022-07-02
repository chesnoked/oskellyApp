package su.reddot.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;
import su.reddot.domain.model.attribute.AttributeValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class ProductAlertAttributeValueBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private ProductAlertSubscription productAlertSubscription;

    @ManyToOne
    @NotNull
    private AttributeValue attributeValue;

}
