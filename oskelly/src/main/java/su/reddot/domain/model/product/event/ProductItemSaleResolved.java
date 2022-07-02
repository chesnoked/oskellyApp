package su.reddot.domain.model.product.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Событие, которые возникает,
 * когда продавец подтверждает или отклоняет продажу вещи
 */
@RequiredArgsConstructor @Getter
public class ProductItemSaleResolved {

    private final Long resolvedItemId;
}
