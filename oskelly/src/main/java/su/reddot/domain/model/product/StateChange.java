package su.reddot.domain.model.product;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;

import javax.persistence.*;
import java.time.ZonedDateTime;

/** Изменение состояния вещи. */
@Entity
@Getter @Setter @Accessors(chain = true)
public class StateChange {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductItem productItem;

    @Enumerated(EnumType.STRING)
    private ProductItem.State newState;

    /** Время появления нового состояния. */
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime at;
}
