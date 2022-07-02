package su.reddot.domain.service.product;

import lombok.Value;

import java.util.List;

/**
 * Страница со списком товаров
 */
@Value
public class CatalogProductPage {
    /**
     * Список товаров на выбранной странице
     */
    private final List<CatalogProduct> products;

    /**
     * Общее число страниц
     */
    private final int totalPages;

    /**
     * Общее число товаров
     */
    private final long productsTotalAmount;
}
