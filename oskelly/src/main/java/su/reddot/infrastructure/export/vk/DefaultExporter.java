package su.reddot.infrastructure.export.vk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.service.product.ProductService;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor;

import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static su.reddot.infrastructure.export.vk.QExportedImage.exportedImage;
import static su.reddot.infrastructure.export.vk.QExportedProduct.exportedProduct;

@Component
@RequiredArgsConstructor @Slf4j
public class DefaultExporter implements Exporter {

    private final ExportedImageRepository   imageRepo;
    private final ExportedProductRepository productRepo;

    private final ProductService    productService;

    private final Client client;
    private final ImageProcessor  imageProcessor;
    private final VkConfiguration conf;

    @Value("${resources.images.pathToDir}")
    private String imageBaseDirPath;

    public void exportImage(Image image) throws Exception {

        boolean imageIsAlreadyExported = imageRepo.exists(
                exportedImage.image.eq(image));

        if (imageIsAlreadyExported) { return; }

        String externalId = exportSingleImage(image);
        ExportedImage exportedImage = new ExportedImage()
                .setImage(image)
                .setExternalId(externalId)
                .setExportedAt(ZonedDateTime.now());

        imageRepo.save(exportedImage);
    }

    @Transactional
    public void exportProduct(Product p) throws Exception {

       Product syncedProduct = productService.getRawProduct(p.getId())
                .orElseThrow(() -> new IllegalStateException(p.getId().toString()));

        // если основное изображение товара еще не экспортировали,
        // то и товар экспортировать нельзя (ограничение апи ВК)
        if (!mainImageIsExported(syncedProduct)) {
            log.debug("Основное изображение еще не загружено для товара: {}, пропускаем",
                    syncedProduct.getId());
        }

        log.debug("Основное изображение уже загружено для товара: {}, продолжаем процесс загрузки товара",
                syncedProduct.getId());

        exportSingleProduct(syncedProduct);
    }

    @Override
    @Transactional
    public void remove(ExportedProduct exportedProduct) throws Exception {

        client.delete(exportedProduct.getExternalId());

        productRepo.delete(exportedProduct);

        /* иначе повторый экспорт удаленного товара не пройдет:
        * photo not found or already assigned to another item */
        List<Image> productImages = productService.getProductImages(exportedProduct.getProduct());
        if (!productImages.isEmpty()) {
            imageRepo.deleteByImageIn(productImages);
        }
    }

    /** Проверить, экспортировали ли ранее главное изображение товара. */
    private boolean mainImageIsExported(Product product) {
        return productService.getPrimaryImage(product)
                .map(i -> imageRepo.exists(exportedImage.image.id.eq(i.getId())))
                .orElse(false);
    }

    /** Экспортировать изображение в ВК. */
    private String exportSingleImage(Image image) throws Exception {

        Path       imagePath  = addWatermarkTo(image);
        SaveResult saveResult = client.uploadImage(imagePath.toFile(), image.isMain());

        return saveResult.getResponse().get(0).getId();
    }

    /** Наложить на данное изображение водяной знак. Полученный результат сохраняется отедльным файлом. */
    private Path addWatermarkTo(Image image) throws Exception {

        Path path = Paths.get(image.getImagePath());

        Path subpathWithoutFilename = path.subpath(0, path.getNameCount() - 1);
        Path filename = path.subpath(path.getNameCount() - 1,
                path.getNameCount());

        Path imageAbsolutePath = Paths.get(imageBaseDirPath, subpathWithoutFilename.toString(), String.format("item-%s", filename));
        Path pathToWatermarkedImage = Paths.get(imageBaseDirPath, subpathWithoutFilename.toString(), String.format("item-%s-wm.jpg", filename));

        if (!Files.exists(pathToWatermarkedImage)) {
            log.info("Файл для отправки не найден: " + pathToWatermarkedImage.toString());
            imageProcessor.addWatermark(imageAbsolutePath.toString());
        }

        return pathToWatermarkedImage;
    }

    private void exportSingleProduct(Product productSuitedForExport) throws Exception {

        ExportedProduct alreadyExportedProduct = productRepo.findOne(exportedProduct.product.eq(productSuitedForExport));

        /* товар раньше не добавляли в ВК */
        if (alreadyExportedProduct == null) {
            log.debug("Товар ранее не экспортировался: {}, продолжаем процесс экспорта.",  productSuitedForExport.getId());
            ProductAddResult result = addProduct(productSuitedForExport);
            TimeUnit.MILLISECONDS.sleep(300);

            Category category = productSuitedForExport.getCategory();
            List<Integer> albums = getAlbumsBy(category);
            String marketItemId = result.getResponse().getMarketItemId();

            for (Integer album : albums) {
                try {
                    client.addToAlbum(marketItemId, album);
                    TimeUnit.MILLISECONDS.sleep(300);
                }
                catch (Exception e) {
                    log.error("Ошибка добавления товара в подбори товаров ВК маркета. Товар: {}",
                            productSuitedForExport.getId(), e);
                }
            }

            ExportedProduct  exportedProduct = new ExportedProduct()
                    .setProduct(productSuitedForExport)
                    .setExternalId(result.getResponse().getMarketItemId())
                    .setExportedAt(ZonedDateTime.now());

            log.debug("Сохраняем успешно экспортированный товар в БД: {}",  productSuitedForExport.getId());
            productRepo.save(exportedProduct);
        }
        else {
            log.debug("Товар уже экспортировался ранее: {}, " +
                    "проверяем наличие новых данных для обновления товара в маркете",
                    productSuitedForExport.getId());

            updateProductIfTheCase(alreadyExportedProduct);
            TimeUnit.MILLISECONDS.sleep(300);
        }
    }

    private List<Integer> getAlbumsBy(Category category) {

        Map<Long, Integer> albumByCategory = conf.getAlbumByCategory();
        List<Integer> foundAlbums = new ArrayList<>();

        List<Category> productCategoryAndItsParents = category.getParents();
        productCategoryAndItsParents.add(category);

        for (Category cat : productCategoryAndItsParents) {
            Integer albumIfAny = albumByCategory.get(cat.getId());
            if (albumIfAny != null) { foundAlbums.add(albumIfAny); }
        }

        return foundAlbums;
    }

    private void updateProductIfTheCase(ExportedProduct alreadyExportedProduct) throws Exception {

        List<Image> actualProductImages = alreadyExportedProduct.getProduct().getImages();

        /* новые экспортированные изображения, которые еще не добавлены в товар маркета */
        List<ExportedImage> newlyExportedImages = new ArrayList<>();
        imageRepo.findAll(exportedImage.image.in(actualProductImages).and(exportedImage.addedToProduct.isFalse()))
                .forEach(newlyExportedImages::add);

        /* если есть новые экспортированные изображения, добавить их в товар маркета. */
        if (newlyExportedImages.isEmpty()) { return; }

        Product actualProduct = alreadyExportedProduct.getProduct();

        /* Внешний идентификатор основного изображения */
        Image primaryImage  = actualProduct.getImages().stream()
                .filter(Image::isMain).findFirst().orElseThrow(IllegalStateException::new);
        ExportedImage primaryExportedImage = imageRepo.findOne(exportedImage.image.eq(primaryImage));

        /* Идентификаторы дополнительных изображений, которые уже добавлены в товар маркета */
        List<Image> secondary = actualProductImages.stream().filter((img) -> !img.isMain()).collect(Collectors.toList());
        List<ExportedImage> secondaryExported = new ArrayList<>();
        imageRepo.findAll(exportedImage.image.in(secondary).and(exportedImage.addedToProduct.isTrue()))
                .forEach(secondaryExported::add);

        secondaryExported.addAll(newlyExportedImages);
        List<String> exportedImagesIds = secondaryExported.stream()
                .map(ExportedImage::getExternalId).collect(Collectors.toList());

        client.edit(new Client.Product()
                .id(alreadyExportedProduct.getExternalId())
                .name(String.format("%s %s", actualProduct.getDisplayName(), actualProduct.getBrand().getName()))
                .description(getDescription(actualProduct))

                .category(getExternalCategoryFor(actualProduct.getCategory())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Не найдена ВК категория для категоррии: "
                                + actualProduct.getCategory().getId())))

                .price(productService.getItemsSinglePriceIfAny(actualProduct)
                        .orElseThrow(() -> new IllegalStateException(actualProduct.getId().toString()))
                        .setScale(2, RoundingMode.UP).toPlainString())
                .deleted(false)
                .mainPhotoId(primaryExportedImage.getExternalId())
                .photoIds(exportedImagesIds)
        );

        primaryExportedImage.setAddedToProduct(true);
        newlyExportedImages.forEach(exportImage -> exportImage.setAddedToProduct(true));
        alreadyExportedProduct.setExportedAt(ZonedDateTime.now());
    }

    /** Добавить товар, у которого <b>уже экспортированы</b> основное изображение */
    private ProductAddResult addProduct(Product product) throws Exception {

        List<Image> all       = productService.getProductImages(product);
        Image       primary   = all.stream().filter(Image::isMain).findFirst().orElseThrow(IllegalStateException::new);
        List<Image> secondary = all.stream().filter((img) -> !img.isMain()).collect(Collectors.toList());

        ExportedImage       primaryExportedImage    = imageRepo.findOne(exportedImage.image.eq(primary));
        List<ExportedImage> secondaryExportedImages = new ArrayList<>();
        imageRepo.findAll(exportedImage.image.in(secondary)).forEach(secondaryExportedImages::add);

        Client.Product vkProduct = new Client.Product()
                .name(String.format("%s %s", product.getDisplayName(), product.getBrand().getName()))
                .description(getDescription(product))

                .category(getExternalCategoryFor(product.getCategory())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Не найдена ВК категория для категории товара: "
                                + product.getCategory().getId())))

                .price(productService.getItemsSinglePriceIfAny(product)
                        .orElseThrow(() -> new IllegalStateException(
                                "У товара отсутствует единая цена, выгрузка невозможна. Товар: "
                                + product.getId().toString()))
                        .setScale(2, RoundingMode.UP).toPlainString())

                .mainPhotoId(primaryExportedImage.getExternalId());

        /* Возможна ситуация, когда на момент добавления товара в ВК его изображения еще не экспортировались в ВК полностью.
        * В этом случае создаваемый в маркете товар будет содержать только те изображения, которые уже успели экспортироваться.
        * Остальные изображения добавятся в созданный товар позднее по мере их загрузки в ВК.
        **/

        if (!secondaryExportedImages.isEmpty()) {
            vkProduct.photoIds(secondaryExportedImages.stream()
                    .map(ExportedImage::getExternalId)
                    .collect(Collectors.toList()));
        }


        return client.add(vkProduct);
    }

    private String getDescription(Product product) {

        String productUrl = "https://oskelly.ru/products/" + product.getId();

        List<Size> availableSizes = productService.getAvailableSizes(product);
        for (Size availableSize : availableSizes) {
            availableSize.getId();
        }

        String productSizes = productService.getAvailableSizes(product).stream()
                .map(size -> size.getBySizeType(
                        product.getSizeType()))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" / "));

        String cookedProductSizes = String.format("%s %s",
                product.getSizeType().getAbbreviation(),
                productSizes);

        String productCondition = product.getProductCondition().getName();
        String nullableCollectionType = product.isNewCollection()? "Новая коллекция" : null;
        String nullableDescription = product.getDescription();

        String nullableClothingMaterial = product.getAttributeValues().stream()
                .filter(b -> b.getAttributeValue().getAttribute().getName().equals("Материал одежды"))
                .findFirst().map(b -> b.getAttributeValue().getValue()).orElse(null);

        String nullableColor = product.getAttributeValues().stream()
                .filter(b -> b.getAttributeValue().getAttribute().getName().equals("Цвет"))
                .findFirst().map(b -> b.getAttributeValue().getValue()).orElse(null);

        return productUrl
                + "\n\nРазмеры: " + cookedProductSizes
                + (nullableClothingMaterial != null? "\nМатериал одежды: " + nullableClothingMaterial : "")
                + (nullableColor != null? "\nЦвет: " + nullableColor : "")
                + ("\nСостояние товара: " + productCondition)
                + (nullableCollectionType != null? "\nКоллекция: " + nullableCollectionType : "")
                + (nullableDescription != null? "\n" + nullableDescription : "");
    }

    private Optional<Client.Category> getExternalCategoryFor(Category c) {

        return Optional.ofNullable(
                c.isWoman() ? Client.Category.woman
                : c.isMan() ? Client.Category.man
                : c.isKids() ? Client.Category.kids
                : null);
    }
}
