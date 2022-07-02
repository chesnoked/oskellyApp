package su.reddot.domain.model.subscription.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Данное событие вызываем при обновлении цены товара
 */
@Getter
@RequiredArgsConstructor
public class PriceUpdateEvent {

    private final Long updatedProductId;

}
