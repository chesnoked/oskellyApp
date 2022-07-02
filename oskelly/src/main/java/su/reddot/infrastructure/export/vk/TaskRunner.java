package su.reddot.infrastructure.export.vk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.service.product.ProductService;

import java.util.List;

@Component
@Profile({"prod"})
@RequiredArgsConstructor @Slf4j
public class TaskRunner {

    private final Exporter                  exporter;
    private final ProductService            productService;
    private final ExportedProductRepository repo;

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 1000 * 10 * 10)
    public void exportProducts() {

        for (Product product : productService.getAvailableProducts()) {

            log.debug("Попытка экспортировать товар: {}", product.getId());
            // непонятно, в какую категорию ВК маркета добавлять товары из "стиля жизни"
            if (product.getCategory().isLifeStyle()) {
                log.debug("Категория товара - стиль жизни, пропускаем товар", product.getId());
                continue;
            }

            try {
                exporter.exportProduct(product);
                log.debug("Попытка экспортировать товар завершилась штатно: {}", product.getId());
            }
            catch (Exception e) {

                log.error("Ошибка добавления товара в ВК. Товар: {} ", product.getId(), e);
            }
        }

        for (ExportedProduct exportedProduct : repo.findObsolete()) {
            try {
                exporter.remove(exportedProduct);
            } catch (Exception e) {
                log.error("Ошибка при попытке удалить товар из маркета. Товар: {}",
                        exportedProduct.getProduct().getId(), e.getMessage(), e);
            }
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void exportImages() {

        List<Product> availableProducts = productService.getAvailableProducts();

        for (Product availableProduct : availableProducts) {

            if (availableProduct.getCategory().isLifeStyle()) {
                continue;
            }

            for (Image image : productService.getProductImages(availableProduct)) {

                log.debug("Попытка экспортировать изображение: {}", image.getId());
                try {
                    exporter.exportImage(image);
                    log.debug("Попытка экспортировать изображение завершилась штатно: {}", image.getId());
                } catch (Exception e) {
                    log.error("Ошибка экспорта изображения в ВК: {}. Изображение: {}",
                            e.getMessage(), image.getId(), e);
                }
            }
        }
    }
}
