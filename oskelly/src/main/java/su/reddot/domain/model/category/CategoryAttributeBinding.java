package su.reddot.domain.model.category;

import lombok.Data;
import su.reddot.domain.model.attribute.Attribute;

import javax.persistence.*;

@Entity
@Data
public class CategoryAttributeBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Attribute attribute;
}
