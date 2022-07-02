package su.reddot.domain.service.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductStatus;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.image.ProductImage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Данные о товаре, необходимые для его отображения в списке товаров
 * выбранной категории
 */
@Getter @Setter
@RequiredArgsConstructor
public class CatalogProduct {

    private final Product product;

    private final ProductImage primaryImage;

    private final List<ProductStatus> statuses;

    /**
     * Первая попавшаяся позиция товара
     * TODO: Дальше выпилить это поле
     */
    private final ProductItem productItem;

    /*FIXME едва ли в представлении товара нужно детальная информация по всем вещам */
    private final List<ProductItem> items;

    /**
     * Размеры вещей, доступных для покупки.
     */
    private SizeSummary sizeSummary;

    private final BigDecimal lowestPrice;

    private final BigDecimal startPriceForLowestPrice;

    /**
     * Нужно для страницы модерации
     * FIXME отдельное представление для страницы модерации
     */
    private String fullPath;

    private Set<ProductSizeMapping> productSizeMappings;

    /**
     * Вещи, которые уже оплачены покупателями и ждут подтверждения продавца
     */
    private List<ProductItem> purchaseRequestedProductItems = Collections.emptyList();

    private boolean isLiked;
    private int likesCount;

    /**
     * Рекомендованная цена и экономия
     * @apiNote nullable
     * */
    private BigDecimal rrp;
    private Integer savingsValue;

    /**
     * У товара не обязательно должен быть размер
     */
    public boolean hasAnySize() { return items.get(0).getSize() != null; }

    /**
     * @return размер вида RUS: 36, 38, 40
     */
    @SuppressWarnings("unused") /* используется в представлении catalog/product_card */
    public String getFormattedSize() {

        String joinedSizes = String.join(" ", sizeSummary.values);

        return String.format("%s: %s", sizeSummary.abbreviation, joinedSizes);
    }

    /**
     * @param sizeType тип размера
     * @return размер вида 36, 38, 40
     */
    public String getSizeValue(SizeType sizeType) {

        if (sizeType == null) { return items.get(0).getConcreteSizePretty().orElse(""); }

        List<String> sizesInConcreteSizeType = items.stream()
                .map(i -> i.getSize().getBySizeType(sizeType))
                .collect(Collectors.toList());

        String joinedSizes = String.join(" / ", sizesInConcreteSizeType);

        if(sizeType == SizeType.NO_SIZE){
            return SizeType.NO_SIZE.getAbbreviation();
        }

        return String.format("%s", joinedSizes);
    }

    @AllArgsConstructor @Getter @Setter
    public static class SizeSummary {

        /** Значения в конкретной размерной сетке */
        private List<String> values = Collections.emptyList();

        private String abbreviation;
    }

}
