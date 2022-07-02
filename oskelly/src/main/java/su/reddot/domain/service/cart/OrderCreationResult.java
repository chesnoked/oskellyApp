package su.reddot.domain.service.cart;

import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.order.Order;

import java.util.List;

@RequiredArgsConstructor
public class OrderCreationResult {

    public final List<CartItem> productItemsThatCanNotBeOrdered;

    public final Order order;
}
