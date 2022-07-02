package su.reddot.domain.service.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductStatus;
import su.reddot.domain.service.image.ProductImage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Данные как о непосредственных свойствах товара, которые
 * хранятся в сущности {@link su.reddot.domain.model.product.Product},
 * так и о включенных в товар данных о его изображениях, статусах и пр.
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
public class DetailedProduct {
    private final Product product;

    private final List<ProductImage> images;

    private final List<ProductStatus> statuses;

    private final List<AttributeValue> attributeValues;

    private final List<ProductItem> productItems;

    /**
     * Нужно для страницы модерации
     * TODO: подумать что с ним делать
     */
    private String fullPath;

    private Set<ProductSizeMapping> productSizeMappings;

    private List<ItemsSummaryBySize> sizes;

    public Optional<ProductImage> getPrimaryImage() {
        return images.stream().filter(ProductImage::isPrimary).findFirst();
    }

    public List<ProductImage> getAdditionalImages() {
        return images.stream().filter((i) -> !i.isPrimary())
                .collect(Collectors.toList());
    }

}
