package su.reddot.domain.model.attribute;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private String transliterateValue;

    @ManyToOne
    private Attribute attribute;
}
