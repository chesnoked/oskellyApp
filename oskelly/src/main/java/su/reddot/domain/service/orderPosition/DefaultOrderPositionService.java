package su.reddot.domain.service.orderPosition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.user.UserService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 15.12.17.
 */
@Component
@RequiredArgsConstructor
public class DefaultOrderPositionService implements OrderPositionService {

	private final OrderPositionRepository orderPositionRepository;

	private final UserService userService;

	private final ImageService imageService;

	@Override
	public List<SaleView> getUserSales(Long sellerId) {
		List<OrderPosition> orderPositions = getOrderPositions(sellerId);
		return from(orderPositions);
	}

	@Override
	public List<SaleGroupView> getGroupedUserSales(Long sellerId) {
		List<OrderPosition> orderPositions = getOrderPositions(sellerId);
		Map<OrderPosition.StateGroup, List<OrderPosition>> map = orderPositions.stream()
				.filter(op -> op.getState().isPresent())
				.collect(
						Collectors.groupingBy(op -> op.getState()
								.map(OrderPosition.State::getGroup)
								.orElseThrow(IllegalArgumentException::new) // если не сработала проверка .filter(op -> op.getState().isPresent())
						)
				);
		return map.keySet().stream()
				.sorted(Comparator.comparing(OrderPosition.StateGroup::getSort))
				.map(k -> new SaleGroupView(k.getDescription(), from(map.get(k))))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<OrderPosition> getById(Long id) {
		return Optional.ofNullable(orderPositionRepository.getOne(id));
	}

	private List<OrderPosition> getOrderPositions(Long sellerId) {
		User seller = userService.getUserById(sellerId).orElseThrow(IllegalArgumentException::new);
		List<OrderState> validStates = Arrays.stream(OrderState.values()).filter(s -> !s.isOrderIsNotPayedYet()).collect(Collectors.toList());
		return orderPositionRepository.findByProductItemProductSellerAndOrderStateIn(seller, validStates);
	}

	private List<SaleView> from(List<OrderPosition> orderPositions) {
		return orderPositions.stream()
				.sorted((o1, o2) -> o2.getId().compareTo(o1.getId())) // сортировка по ID в обратном порядке
				.map(op -> {
					ProductItem productItem = op.getProductItem();
					Product p = productItem.getProduct();
					Optional<String> imageUrl = imageService.getPrimaryImage(p).map(ProductImage::getUrl);
					return new SaleView(
							op.getId(),
							op.getOrder().getOrderId(),
							p.getId(),
							productItem.getId(),
							p.getBrand().getName(),
							p.getCategory().getDisplayName(),
							productItem.getConcreteSizePretty().orElse(null),
							productItem.getCurrentPrice(),
							op.getAmount(),
							op.getAmount().multiply(BigDecimal.ONE.subtract(op.getCommission())),
							imageUrl.orElse(null),
							op.getState().map(OrderPosition.State::getDescription).orElse(null),
							OrderPosition.State.PURCHASE_REQUEST_SENT.equals(op.getState().orElse(null))
					);
		}).collect(Collectors.toList());
	}
}
