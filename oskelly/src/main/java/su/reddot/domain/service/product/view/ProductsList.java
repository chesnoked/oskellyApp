package su.reddot.domain.service.product.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor @Getter
public class ProductsList {

    private final List<ProductCard> products;

    private final int totalPages;

    private final long productsTotalAmount;
}
