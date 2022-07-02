package su.reddot.domain.service.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.reddot.domain.model.size.Size;

import java.math.BigDecimal;

@RequiredArgsConstructor @Getter
public class ItemsSummaryBySize {

        private final Size size;

        private final Long count;

        private final boolean isWithDistinctPrices;

        /**
         * Точное значение минимальной стоимости товаров данного размера
         */
        private final BigDecimal lowestPrice;

        /*
         * Начальная цена товара без скидки
         */
        private final BigDecimal startPrice;
}
