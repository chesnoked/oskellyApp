package su.reddot.infrastructure.cashregister;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Если для компонента нужно рапечатать чек,
 * то компонент должен предоставлять информацию,
 * которая необходима для построения чека.
 */
public interface Checkable {

    String getRequestId();

    BigDecimal getNonCashAmount();

    List<Line>  getLines();

    @RequiredArgsConstructor @Getter
    class Line {

        /** Количество купленного товара */
        private final float quantity;

        /** Цена с точостью до копеек */
        private final BigDecimal price;

        /** Наименование товарной позиции. Не может быть пустым */
        private final String description;
    }
}
