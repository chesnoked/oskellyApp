package su.reddot.domain.service.publication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.product.Product;

@RequiredArgsConstructor @Getter
public class PriceChangedEvent {
    private final Product productWithAlteredPrice;
}
