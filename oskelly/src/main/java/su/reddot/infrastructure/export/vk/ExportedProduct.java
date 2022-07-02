package su.reddot.infrastructure.export.vk;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.product.Product;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter @Setter @Accessors(chain = true)
class ExportedProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Product product;

    /** Идентификатор товара на внешнем ресурсе. */
    private String externalId;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime exportedAt;

}
