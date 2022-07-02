package su.reddot.domain.service.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.order.OrderPosition;

import java.time.ZonedDateTime;

@Getter @Setter @Accessors(chain = true)
public class OrderPositionStateChange {

    private ZonedDateTime at;

    private OrderPosition.State state;
}
