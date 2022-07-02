package su.reddot.domain.service.admin.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.dao.order.OrderRepository;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.order.view.OrderItem;
import su.reddot.domain.service.profile.ProfileService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static su.reddot.domain.model.order.OrderState.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminOrderService implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderPositionRepository orderPositionRepository;
    private final ImageService imageService;
    private final ProfileService profileService;

    @Override
    public List<AdminOrderView> findAllActive() {
        List<OrderState> activeStates = Arrays.asList(HOLD, HOLD_COMPLETED);
        List<Order> orders = orderRepository.findAllByStateInOrderByStateTimeDesc(activeStates);

        return orders.stream().map(this::of).collect(Collectors.toList());
    }

    @Override
    public List<AdminOrderView> findAllInWarningState() {
        List<OrderState> warningStates = Arrays.asList(HOLD_ERROR, CANCELED, HOLD_PROCESSING, REFUND);
        List<Order> orders = orderRepository.findAllByStateInOrderByStateTimeDesc(warningStates);

        return orders.stream().map(this::of).collect(Collectors.toList());
    }

    private AdminOrderView of(Order order) {
        AdminOrderView adminOrderView = new AdminOrderView(
                order.getId(),
                order.getAmount(),
                order.getState().getDescription(),
                order.getState().name(),
                order.getStateTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"))),
                profileService.getProfileView(order.getBuyer().getId()),
                order.isPayable(),
                order.isPaidAtThisMoment(),
                order.getDeliveryRequisite()
        );

        for (OrderPosition orderPosition : order.getOrderPositions()) {
            ProductItem p = orderPosition.getProductItem();
            Optional<String> imageUrl = imageService.getPrimaryImage(p.getProduct()).map(ProductImage::getUrl);

            OrderItem orderItem = new OrderItem(
                    orderPosition.getId(),
                    p.getProduct().getId(),
                    p.getId(),
                    imageUrl.orElse(null),
                    p.getProduct().getBrand().getName(),
                    p.getProduct().getDisplayName(),
                    orderPosition.getAmount().setScale(2, RoundingMode.UP),
                    p.getConcreteSizePretty().orElse(null),
                    p.getProduct().getSeller().getNickname(),
                    p.getProduct().getSeller().getId(),
                    p.getState().name(),
                    Optional.ofNullable(orderPosition.getDeliveryCost()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.UP)
            );

            adminOrderView.getItems().add(orderItem);
        }

        return adminOrderView;
    }
}
