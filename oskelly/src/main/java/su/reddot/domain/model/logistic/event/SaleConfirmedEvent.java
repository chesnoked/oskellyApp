package su.reddot.domain.model.logistic.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.logistic.DestinationType;

/**
 * @author Vitaliy Khludeev on 27.08.17.
 */
@Getter
@RequiredArgsConstructor
public class SaleConfirmedEvent {

	private final Long orderPositionId;

	private final DestinationType pickupDestinationType;

	private final DestinationType deliveryDestinationType;
}
