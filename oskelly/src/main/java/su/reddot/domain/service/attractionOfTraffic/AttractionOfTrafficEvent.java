package su.reddot.domain.service.attractionOfTraffic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.order.Order;
import su.reddot.infrastructure.acquirer.Payable;

/**
 * @author Vitaliy Khludeev on 10.10.17.
 */
@RequiredArgsConstructor
@Getter
public class AttractionOfTrafficEvent {
	private final Payable payable;
}
