package su.reddot.infrastructure.export.vk;

import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;

public interface Exporter {

    void exportImage(Image i) throws Exception;

    void exportProduct(Product p) throws Exception;

    /** Удалить ранее экспортированный товар из ВК. */
    void remove(ExportedProduct exportedProduct) throws Exception;
}
