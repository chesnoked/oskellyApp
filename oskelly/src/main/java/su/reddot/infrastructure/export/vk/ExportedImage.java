package su.reddot.infrastructure.export.vk;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.product.Image;

import javax.persistence.*;
import java.time.ZonedDateTime;

/** Данные о результатах загрузки изображения на внешний ресурс. */
@Entity
@Getter @Setter @Accessors(chain = true)
public class ExportedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Image image;

    /** Идентификатор изображения на внешнем ресурсе. */
    private String externalId;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime exportedAt;

    /** Изображение привязано к добавленному в маркет товару */
    private boolean addedToProduct;
}
