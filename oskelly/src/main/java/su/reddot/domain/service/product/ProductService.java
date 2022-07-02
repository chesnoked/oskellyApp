package su.reddot.domain.service.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.product.*;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.catalog.AvailableFilters;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.product.view.OfferView;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.presentation.view.product.ProductView;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    /**
     * Получить товар по его идентификатору, если такой товар существует
     * @param id идентификатор товара
     * @return данные о товаре
     */
    Optional<DetailedProduct> getProduct(Long id);
    Optional<Product> getRawProduct(Long id);
    List<Image> getProductImages(Product p);

    /**
     * Получить страницу с выборкой товаров со стандартным размером страницы
     * @param spec спецификация выборки {@link FilterSpecification}
     * @param page номер страницы
     * @param sortAttribute тип сортировки найденных товаров {@link SortAttribute}
     * @return краткая информация о выбранных товарах
     *
     * @see #getProductsList(FilterSpecification, int, SortAttribute)
     */
    @Deprecated
    CatalogProductPage getProducts(FilterSpecification spec, int page, SortAttribute sortAttribute);

    /**
     * {@link ProductService#getProducts(FilterSpecification, int, SortAttribute)}
     * с возможностью задания дополнительных настроек отображения страницы товаров
     * @param q настройки отображения товаров
     *
     * @see #getProductsList(FilterSpecification, int, SortAttribute, ViewQualification)
     */
    @Deprecated
    CatalogProductPage getProducts(FilterSpecification spec, int page, SortAttribute sortAttribute, ViewQualification q);

    ProductsList getProductsList(FilterSpecification spec, int page, SortAttribute sortAttribute);

    ProductsList getProductsList(FilterSpecification spec, int page,
                                 SortAttribute sortAttribute,
                                 ViewQualification q);

    AvailableFilters getAvailableFilters(FilterSpecification spec, Long categoryId);

    List<ProductCard> getProductCardsByProducts(List<Product> rawProducts, User user);

    /**
     *
     * @param id идентификатор товара
     * @param nullableUser пользователь, который запросил данные о товаре
     * @return данные о товаре, которые нужны для его отображения на странице товара
     */
    Optional<ProductView> getProductView(Long id, User nullableUser);

    void updateState(Long productId, ProductState state);

    List<ProductCondition> getActualConditions(List<Long> categories);

    /**
     * @return только те бренды, товары которых есть в выбранных категориях.
     */
    List<Brand> getActualBrands(List<Long> categories);

    boolean getVintageProductsPresence(List<Long> categories);
    boolean getNewCollectionProductsPresence(List<Long> categories);
    boolean getProductsWithOurChoicePresence(List<Long> categories);
    boolean getSaleProductsPresence(List<Long> categories);

    /**
     * Имеет ли право пользователь редактировать товар (поменить как "Снятый с продажи" и редактировать цену)
     * @param id товара
     * @param nullableUser пользователь
     * @return true если пользователь может снять товар с продажи
     */
    boolean canUserEditProduct(Long id, User nullableUser);

    /**
     * Меняет статус товара на DELETED
     * @param product товар
     */
    void delete(Product product);

    /**
     * @return только те значения атрибутов, которые присущи товарам в указанной категории.
     */
    Map<Attribute, List<AttributeValue>> getActualAttributeValues(Long categoryId);

    /**
     * @return только те значения размеров, которые присущи товарам в указанной категории.
     */
    List<CatalogSize> getActualSizes(Long c);

    /**
     * Вариация {@link #findProductsByFullTextSearch(String, int)}, которая возвращает
     * результаты поиска в виде готовых к отображению карточек товаров
     * @return найденные товары в виде карточек
     */
    List<ProductCard> findProducts(String query, int limit);

    /**
     * @param limit ограничение на кол-во возвращаемых элементов
     * @return найденные товары в виде, позволяющем выводить suggestions
     * в интерфейсе пользователя
     */
    List<FullTextSearchProductView> findProductsByFullTextSearch(String query, int limit);

    /**
     * Иногда надо получить товары Продавца прямо из даннго сервиса.
     * @param seller
     * @param limit
     * @return найденные товары в виде карточек
     */
    List<ProductCard> findSomeBySeller(User seller, int limit);
    /**
     * @return состояние товара "с биркой"
     */
    /**
     *  Сколько товаров продает
     * @param user
     * @return
     */
    Integer countBySeller(User user);

    Optional<ProductCondition> getNewCondition();


    /**
     *
     * @param productId
     * @return список всех размеров в данной категории
     */
    List<Size> getSameProductsSizeChart(Long productId);


    /**
     * @return число товаров (именно товаров, не вещей), опубликованных за последнюю неделю
     * @apiNote метод не отличается гибкостью и не позволяет считать число товаров
     * по произвольному критерию. Но тем не менее его возможностей пока достаточно.
     */
    int getLastWeekPublishedProductsCount();

    /** Предложить новую цену на товар.
     * @param newPrice (not null) предлагаемая цена.
     * @param user (not null) пользователь, который предлагает цену.
     * @throws PriceNegotiationException если текущее состояние товара не позволяет вести торг, или торг невозможен для данного пользователя по данной цене.
     * @throws NotFoundException если товар - предмет торга не существует
     * */
    void makeAnOffer(Long id, BigDecimal newPrice, User user)
            throws PriceNegotiationException, NotFoundException;

    /**
     *
     * @return Согласованная в процессе торгов цену на товар, если таковая существует.
     */
    Optional<BigDecimal> getNegotiatedPrice(Product product, User u);

    /** Подтвердить или отклонить предложение цены на товар может только продавец товара. */
    void confirmOffer(Long offerId, User seller, boolean doConfirmOffer);


    /** Предложение о снижении цены для его отображения на сайте. */
    Optional<OfferView> getProductOffer(long id, User seller);

    /** Получить число товаров на продажу у данного пользователя - продавца.
     * Учитываются только те товары, которые доступны для покупки. */
    int getAvailableProductsCount(User seller);
    List<Product> getAvailableProducts();

    Optional<ProductImage> getPrimaryImage(Product p);

    /** Возвращает цену вещей в случае, если все вещи имеют одинаковую цену. */
    Optional<BigDecimal> getItemsSinglePriceIfAny(Product p);

    /** Товар можно купить */
    boolean productIsAvailable(Product p);

    /** Список размеров товаров, которые можно купить. */
    List<Size> getAvailableSizes(Product p);

    /** История изменений состояния данной вещи. */
    List<ItemStateChange> getStateHistory(ProductItem productItem);

    @RequiredArgsConstructor @Getter
    enum SortAttribute {

        /**
         * ID товара
         */
        ID ("id"),

        /**
         * ID товара
         */
        ID_DESC ("id_desc"),

        /**
         * Сортировать товары по возрастантию цены
         */
        PRICE ("price"),

        /**
         * Сортировать товары по убыванию цены
         */
        PRICE_DESC ("price_desc"),

        /**
         * Сортировать товары от новых к старым
         */
        PUBLISH_TIME_DESC ("publish_time_desc"),

        /**
         * от старых к новым
         */
        PUBLISH_TIME ("publish_time");

        public static Optional<SortAttribute> of(String nullableParameterName) {

            for (SortAttribute sortAttribute : SortAttribute.values()) {
                if (sortAttribute.getParameterName().equals(nullableParameterName)) {
                    return Optional.of(sortAttribute);
                }
            }

            return Optional.empty();
        }

        /**
         * Название параметра http запроса, которое соответствует конкретному атрибуту сортировки
         */
        private final String parameterName;
    }

    /**
     * Перечень атрибутов, по которым можно отфильтровать множество товаров.
     * Если атрибут задан в виде списка, то фильтрация товара по этому атрибуту
     * происходит с использованием операции ИЛИ: чтобы попасть в результат выборки,
     * товар должен удовлетворять хотя бы одному значению атрибута.
     */
    @Getter @Setter
    @Accessors(fluent = true, chain = true)
    class FilterSpecification {

        /**
         * Список категорий, к которым должен принадлежать товар.
         * При выборке используется оператор ИЛИ:
         * для заданных категорий [1, 2, 3] будет выбран товар,
         * который находится в категории 1 ИЛИ товар, который находится
         * в категории 2 ИЛИ товар, который находится в категории 3
         */
        private List<Long> categoriesIds = Collections.emptyList();

        /**
         * Состояние, в котором должен находиться товар, чтобы попасть в выборку
         */
        private ProductState state;

        /**
         * Состояние, в котором должна находиться вещь, чтобы ее данные попали в выборку
         */
        private ProductItem.State itemState;

        /**
         * Список конкретных значений атрибутов, которыми должен обладать товар.
         */
        private List<Long> interestingAttributeValues = Collections.emptyList();

        /**
         * Список размеров, которые должен иметь товар.
         */
        private List<Long> interestingSizes = Collections.emptyList();

        /**
         * Список брендов, одному из которых должен принадлежать товар
         */
        private List<Long> interestingBrands = Collections.emptyList();

        /**
         * Список интересущих состояний товара
         */
        private List<Long> interestingConditions = Collections.emptyList();

        /**
         * Идентификатор продавца, которому должен принадлежать товар
         */
        private Long sellerId;

        private Boolean isPro;

        private Boolean isVip;

        private BigDecimal startPrice;

        private BigDecimal endPrice;

        private Boolean isDescriptionExists;

        /* -- Фильтровать по наличию булевого флага (тега) -- */

        /** Только винтажные товары */
        private Boolean isVintage;

        /** Только товары, которые продаются по сниженной цене */
        private Boolean isOnSale;

        /** Только товары с отметкой "Наш выбор" */
        private Boolean hasOurChoice;

        /** Только товары с отметкой "Новая коллекция" */
        private Boolean isNewCollection;

        private String sellerEmailSubstring;
    }

    /**
     * Дополнительная настройка отображения товаров
     */
    @Getter @Setter @Accessors(fluent = true, chain = true)
    class ViewQualification {

        /** число товаров на странице */
        private Integer pageLength;

        /** сетка, в которой отображать размеры */
        private SizeType interestingSizeType;

        /** пользователь, для которого нужно персонализировать страницу товаров */
        private User interestingUser;

        /** получить экономию по сравнению с рекомендованной розничной ценой. */
        private boolean withSavings;
    }

    @Getter
    @RequiredArgsConstructor
    class FullTextSearchProductView {
        final String groupName;
        final String image;
        final String title;
        final String keywords;
        final String description;
        final BigDecimal price;
        final String url;
    }
}
