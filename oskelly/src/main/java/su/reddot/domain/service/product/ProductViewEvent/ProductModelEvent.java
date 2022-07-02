package su.reddot.domain.service.product.ProductViewEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import java.util.Map;

/** Запрос на получение сведений о товаре. */
@RequiredArgsConstructor @Getter
public class ProductModelEvent {

    private final Product product;

    private final User nullableUser;

    private final String nullableGuestToken;

    private final Map<String, Object> view;
}
