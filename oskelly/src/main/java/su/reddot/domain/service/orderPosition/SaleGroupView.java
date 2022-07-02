package su.reddot.domain.service.orderPosition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 15.12.17.
 */
@RequiredArgsConstructor
@Getter
public class SaleGroupView {

	private final String groupName;

	private final List<SaleView> sales;
}
