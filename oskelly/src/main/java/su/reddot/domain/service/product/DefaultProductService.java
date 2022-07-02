package su.reddot.domain.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.dao.product.*;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.notification.ModerationFailedNotification;
import su.reddot.domain.model.notification.ModerationPassedNotification;
import su.reddot.domain.model.notification.ProductHiddenNotification;
import su.reddot.domain.model.notification.ProductPublishedNotification;
import su.reddot.domain.model.notification.offer.NewOfferNotification;
import su.reddot.domain.model.notification.offer.OfferAcceptedNotification;
import su.reddot.domain.model.notification.offer.OfferRejectedNotification;
import su.reddot.domain.model.product.*;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.catalog.AvailableFilters;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.catalog.size.SizeView;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.like.LikeService;
import su.reddot.domain.service.notification.NotificationPackage;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.view.OfferView;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.product.view.ProductCard.PurchaseRequestedItem;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.subscription.SubscriptionService;
import su.reddot.domain.service.wishlist.WishListService;
import su.reddot.infrastructure.sender.NotificationSender;
import su.reddot.presentation.Utils;
import su.reddot.presentation.view.SellerView;
import su.reddot.presentation.view.product.BreadcrumbsView;
import su.reddot.presentation.view.product.DescriptionAttributeView;
import su.reddot.presentation.view.product.ProductView;
import su.reddot.presentation.view.product.ProductView.OfferRelated;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static su.reddot.domain.model.product.QProductItem.productItem;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final static int DEFAULT_PAGE_LENGTH = 60;

    private final ProductRepository                      productRepository;
    private final ProductItemRepository                  productItemRepository;
    private final ProductStatusBindingRepository         productStatusBindingRepository;
    private final ProductAttributeValueBindingRepository productAttributeValueBindingRepository;
    private final AttributeValueRepository               attributeValueRepository;
    private final ProductConditionRepository             productConditionRepository;
    private final OfferRepository                        offerRepository;
    private final SizeRepository                         sizeRepository;
    private final CategoryRepository                     categoryRepository;
    private final StateChangeRepository                  stateChangeRepository;

    private final ImageService        imageService;
    private final ProductItemService  productItemService;
    private final WishListService     wishListService;
    private final CategoryService     categoryService;
    private final LikeService         likeService;
    private final SubscriptionService subscriptionService;
    private final CommissionService   commissionService;
    private final NotificationSender  sender;

    private FollowingService    followingService;

    private final ApplicationEventPublisher publisher;

    private final ObjectMapper   mapper;
    private final TemplateEngine templateEngine;

    @Value("${app.host}")
    private String appHost; /* доменное имя для правильной генерации ссылок на ресурсы приложения */


    @Autowired // циклическая зависимость
    public void setFollowingService(FollowingService fs) {
        followingService = fs;
    }

    @Override
    public Optional<DetailedProduct> getProduct(Long id) {
        Product product = productRepository.findOne(id);
        if (product == null) {
            return Optional.empty();
        }

        /* Изображения товара */
        List<ProductImage> productImages = imageService.getProductImages(product);

        /* Статусы товара */
        List<ProductStatus> productStatuses
                = productStatusBindingRepository.findByProduct(product);

        /* Значения атрибутов товара */
        List<AttributeValue> productAttributeValues
                = productAttributeValueBindingRepository
                .findAttributeValuesByProductWithLimit(product, 100);

        List<ProductItem> productItems = productItemService.findByProduct(product);

        return Optional.of(new DetailedProduct(
                product,
                productImages,
                productStatuses,
                productAttributeValues,
                productItems
        ));
    }

    @Override
    public Optional<Product> getRawProduct(Long id) { return Optional.of(productRepository.findOne(id)); }
    public List<Image> getProductImages(Product p) { return imageService.getRawImages(p); }

    @Override
    public CatalogProductPage getProducts(FilterSpecification spec, int page, SortAttribute sortAttribute) {
        return getProducts(spec, page, sortAttribute, new ViewQualification().pageLength(DEFAULT_PAGE_LENGTH));
    }

    @Override
    public CatalogProductPage getProducts(FilterSpecification spec, int page,
                                          SortAttribute sortAttribute,
                                          ViewQualification q) {

        Page<Product> rawProducts = getRawProducts(spec, page, sortAttribute, q);

        return cookProductsPage(rawProducts, spec.interestingSizes(), q.interestingUser(), q.interestingSizeType(), q.withSavings());
    }

    @Override
    public ProductsList getProductsList(FilterSpecification spec, int page, SortAttribute sortAttribute) {
        return getProductsList(spec, page, sortAttribute, new ViewQualification().pageLength(DEFAULT_PAGE_LENGTH));
    }

    @Override
    public ProductsList getProductsList(FilterSpecification spec, int page,
                                        SortAttribute sortAttribute,
                                        ViewQualification q) {

        Page<Product> rawProducts = getRawProducts(spec, page, sortAttribute, q);

        List<ProductCard> productCards = cookProductCards(rawProducts.getContent(), spec.interestingSizes(),
                q.interestingUser(), q.interestingSizeType(), q.withSavings());

        return new ProductsList(
                productCards,
                rawProducts.getTotalPages(),
                rawProducts.getTotalElements()
        );

    }


    /**
     * Возвращяет доступные фильтры при клике на которые выборка не будет пустой
     * возможно это нужно вынести в отдельный сервис
     * проверка идет по ветке категорий
     */
    @Override
    public AvailableFilters getAvailableFilters(FilterSpecification spec, Long categoryId) {
        AvailableFilters availableFilters = new AvailableFilters();
        //Получаем группу аттрибутов со значениями, которые указаны у нас в фильтрах (не все). Аттрибуты с пустым списком тоже выбираем.
        Map<Attribute, List<AttributeValue>> attributeValuesMap = groupAttributeValues(spec.interestingAttributeValues(), categoryId);

        attributeValuesMap.forEach((attribute, attributeValues)->{
            // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме этого аттрибута
            BooleanBuilder booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, false, attribute, false, false, false, false);
            // Делаем выборку товаров по которой заполним доступные значения только этого атрибута
            Page<Product> rawProducts = productRepository.find(booleanBuilder, null);
            rawProducts.forEach(rawProduct->{
                rawProduct.getAttributeValues().forEach(productAttributeValueBinding->{
                    AttributeValue attributeValue = productAttributeValueBinding.getAttributeValue();
                    //мы в нужной группе, остальные заполнять не нужно
                    if(attributeValue.getAttribute()==attribute) {
                        if(attributeValue!=null && !availableFilters.getFilter().contains(attributeValue.getId()))
                            availableFilters.getFilter().add(attributeValue.getId());
                    }
                });
            });
        });

        BooleanBuilder booleanBuilder;
        Page<Product> rawProducts;
        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме размеров
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, true, false, false, null, false, false, false, false);
        // Делаем выборку товаров по которой заполним доступные значения только размеров
        rawProducts = productRepository.find(booleanBuilder, null);
        rawProducts.forEach(rawProduct->{
            rawProduct.getProductItems().forEach(productItem->{
                Size size = productItem.getSize();
                if(size!=null && !availableFilters.getSize().contains(size.getId())) availableFilters.getSize().add(size.getId());
            });
        });

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме брендов
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, true, false, null, false, false, false, false);
        // Делаем выборку товаров по которой заполним доступные значения только брендов
        rawProducts = productRepository.find(booleanBuilder, null);
        rawProducts.forEach(rawProduct->{
            Brand brand = rawProduct.getBrand();
            if(brand!=null && !availableFilters.getBrand().contains(brand.getId())) availableFilters.getBrand().add(brand.getId());
        });

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме состояний
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, true, null, false, false, false, false);
        // Делаем выборку товаров по которой заполним доступные значения только состояний
        rawProducts = productRepository.find(booleanBuilder, null);
        rawProducts.forEach(rawProduct->{
            ProductCondition productCondition = rawProduct.getProductCondition();
            if(productCondition!=null && !availableFilters.getProductCondition().contains(productCondition.getId())) availableFilters.getProductCondition().add(productCondition.getId());
        });

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме vintage
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, false, null, true, false, false, false);
        // Делаем выборку товаров по которой заполним доступные значения только vintage
        rawProducts = productRepository.find(booleanBuilder, null);
        for(Product rawProduct: rawProducts){
            if(rawProduct.isVintage()) {
                availableFilters.setVintage(true);
                break;
            }
        }

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме OnSale
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, false, null, false, true, false, false);
        // Делаем выборку товаров по которой заполним доступные значения только OnSale
        rawProducts = productRepository.find(booleanBuilder, null);
        outerloop:
        for(Product rawProduct: rawProducts){
            //filterExpression.and(productItem.currentPrice.lt(productItem.startPrice));
            for (ProductItem productItem: rawProduct.getProductItems()) {
                if(productItem.getCurrentPrice().doubleValue()<productItem.getStartPrice().doubleValue()) {
                    availableFilters.setOnSale(true);
                    break outerloop;
                }
            }
        }

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме OurChoice
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, false, null, false, false, true, false);
        // Делаем выборку товаров по которой заполним доступные значения только Our Choice
        rawProducts = productRepository.find(booleanBuilder, null);
        for(Product rawProduct: rawProducts){
            if(rawProduct.isOurChoice()) {
                availableFilters.setHasOurChoice(true);
                break;
            }
        }

        // Делаем широкую выборку по ветке категорий из тех фильтров, что выбраны, кроме New Collection
        booleanBuilder = cookFilterExpressionExceptOne(spec, attributeValuesMap, false, false, false, null, false, false, false, true);
        // Делаем выборку товаров по которой заполним доступные значения только New Collection
        rawProducts = productRepository.find(booleanBuilder, null);
        for(Product rawProduct: rawProducts){
            if(rawProduct.isNewCollection()) {
                availableFilters.setNewCollection(true);
                break;
            }
        }

        return availableFilters;
    }
    /**
     * Группируем значения аттрибутов по группам (Атрибутам)
     * ри этом нам нужны все аттрибуты, даже, если списки их значений будут пустыми
     * @param attributeValueIds
     * @return
     */
    private Map<Attribute, List<AttributeValue>> groupAttributeValues(List<Long> attributeValueIds, Long categoryId) {
        //получаем полную выборку аттрибутов по данной категории
        Map<Attribute, List<AttributeValue>> actualAttributeValues = getActualAttributeValues(categoryId);
        //Сюда будем складывать результат
        Map<Attribute, List<AttributeValue>> result = new HashMap<Attribute, List<AttributeValue>>();
        actualAttributeValues.forEach((attribute, values)->{
            //Список, который будет содержать только id из переданного массива значений аььрибутов
            List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
            //Пробегаемся по всем значения аттрибутов, чтобы проверить, есть ли они в нашем списке
            values.forEach(attributeValue ->{
                //если мы наткнулись на аттрибут из нашего списка, то добавляем его в выборку
                if(isLongInList(attributeValueIds, attributeValue.getId())) {
                    attributeValues.add(attributeValue);
                }
            });
            result.put(attribute, attributeValues);
        });
        return result;
    }
    /**
     * Возвращает есть ли объект Long в списке
     * @return
     */
    private boolean isLongInList(List<Long> list, Long needle) {
        for(Long l : list) {
            if(l.longValue()==needle.longValue()) return true;
        }
        return false;
    }

    // эта шняга нужна для простого построения wishList'а и списка фаворитов
    @Override
    public List<ProductCard> getProductCardsByProducts(List<Product> rawProducts, User user){

        return cookProductCards(rawProducts,null, user, null, false);
    }

    public List<Size> getAvailableSizes(Product p) {

        if (p.getSizeType() == SizeType.NO_SIZE) { return Collections.emptyList(); }

        return productItemService.getAvailableItemsSummary(p).stream()
                .map(ItemsSummaryBySize::getSize).collect(Collectors.toList());
    }

    @Override
    public List<ItemStateChange> getStateHistory(ProductItem productItem) {

        QStateChange stateChange = QStateChange.stateChange;

        Iterable<StateChange> states = stateChangeRepository.findAll(
                stateChange.productItem.eq(productItem), stateChange.at.asc());

        List<ItemStateChange> cookedStates = new ArrayList<>();
        for (StateChange state : states) {
            cookedStates.add(
            new ItemStateChange()
                    .setAt(state.getAt())
                    .setState(state.getNewState())
            );
        }

        return cookedStates;
    }

    private List<ProductCard> cookProductCards(List<Product> rawProducts, List<Long> interestingSizes,
                                               User forNullableUser, SizeType interestingSizeType,
                                               boolean withSavings) {

       List<ProductCard> cookedProducts = new ArrayList<>();

        for (Product rawProduct : rawProducts) {
            ProductCard productCard = new ProductCard()
                    .setId(rawProduct.getId())
                    .setName(rawProduct.getDisplayName())
                    .setCategory(rawProduct.getCategory().getDisplayName())
                    .setSingularCategoryName(rawProduct.getCategory().getSingularName())
                    .setBrand(rawProduct.getBrand().getName())
                    .setVintage(rawProduct.isVintage())
                    .setOurChoice(rawProduct.isOurChoice())
                    .setNotUsedYet(rawProduct.isNotUsedYet())
                    .setState(rawProduct.getProductState());

            boolean productIsOnRevisionNow = rawProduct.getProductState() == ProductState.DRAFT
                    || rawProduct.getProductState() == ProductState.REJECTED;
            productCard.setEffectiveUrl(productIsOnRevisionNow? "/publication/properties/" + rawProduct.getId()
                                                                :"/products/" + rawProduct.getId());

            Optional<ProductImage> primaryImage = imageService.getPrimaryImage(rawProduct);
            primaryImage.ifPresent(image -> productCard.setPrimaryImageUrl(image.getUrl()));

            List<ItemsSummaryBySize> availableItemsSummary
                    = productItemService.getAvailableItemsSummary(rawProduct, interestingSizes);

            BigDecimal lowestPrice = getLowestPrice(availableItemsSummary);
            productCard.setLowestPrice(lowestPrice);

            BigDecimal rrpPrice = rawProduct.getRrpPrice();
            if (rrpPrice != null && withSavings) {
                productCard.setRrp(rrpPrice);
                productCard.setSavingsValue(savings(rrpPrice, lowestPrice));
            }

            BigDecimal startPriceForLowestPrice = getStartPriceForLowestPrice(availableItemsSummary);
            productCard.setStartPriceForLowestPrice(startPriceForLowestPrice);

            if (forNullableUser != null) { personalize(productCard, rawProduct, forNullableUser); }

            if (rawProduct.getSizeType() != SizeType.NO_SIZE) {

                SizeType effectiveSizeType = interestingSizeType != null? interestingSizeType : rawProduct.getSizeType();
                List<String> availableSizes = availableItemsSummary.stream()
                        .map(summary -> summary.getSize().getBySizeType(effectiveSizeType))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (availableSizes.size() > 0) {
                    productCard.setSizeSummary(new ProductCard.SizeSummary(availableSizes,
                            effectiveSizeType.getAbbreviation()));
                }
            }

            productCard.setLikesCount(likeService.countByLikeable(rawProduct));

            cookedProducts.add(productCard);
        }

        return cookedProducts;
    }

    private Page<Product> getRawProducts(FilterSpecification spec, int page,
                                         SortAttribute sortAttribute, ViewQualification q) {

        BooleanBuilder filterExpression = cookFilterExpression(spec);

        Sort sort = of(sortAttribute);

        int pageLength = q.pageLength() != null? q.pageLength() : DEFAULT_PAGE_LENGTH;

        return productRepository.find(
                filterExpression,
                new PageRequest(page - 1, pageLength, sort));
    }

    @Override
    public Optional<ProductView> getProductView(Long id, User nullableUser) {

        Product product = productRepository.findOne(id);
        if (product == null || !product.canBeViewedByUser(nullableUser)) { return Optional.empty(); }

        ProductView view = new ProductView();
        view.setId(product.getId())
                .setName(product.getDisplayName())
                .setDescription(product.getDescription())
                .setCondition(product.getProductCondition().getName())
                .setCategory(product.getCategory().getNameForProduct())
                .setBreadcrumbs(getBreadcrumbsView(product))
                .setAttributes(getAttributes(product))
				.setBrand(product.getBrand().getName())
                .setBrandUrl(product.getBrand().getUrl())
                .setSizeType(product.getSizeType())
                .setPrimaryImage(imageService.getPrimaryImage(product).orElse(null))
                .setAdditionalImages(getAdditionalImages(product))
                //TODO отрефакторить на использование ProfileView, т.к. он более функционален
                .setSeller(getSellerView(product.getSeller()))
				.setVintage(product.isVintage())
                .setNewCollection(product.isNewCollection())
                //Данные по WishList
                .setInWishList(wishListService.hasProduct(product, nullableUser))
                .setCountUsersAddedToWishList(wishListService.countByProduct(product))
                //Данные для Like
                .setLiked(nullableUser != null && likeService.doesUserLike(product, nullableUser))
                .setLikesCount(likeService.countByLikeable(product))
                //Есть подписка на снижение цены
                .setPriceSubscription(subscriptionService.isPriceSubscriptionExist(nullableUser, product))
                .setCountUsersSubscribedOnPrice(subscriptionService.countAllPriceSubscribers(product))
                .setState(product.getProductState().getName());

        //Seller уставновлен. Нужно определить, подписан ли просматривающий пользователь на него
        view.getSeller().setIsFollowedContext(nullableUser != null && followingService.isFollowingExists(nullableUser, view.getSeller().getOriginalUser()));


        if (product.isNotUsedYet()) {
            view.getBadges().add(product.getProductCondition().getName());
        }

        if (product.getRrpPrice() != null){ view.setRrp(product.getRrpPrice());}

        List<ItemsSummaryBySize> availableItemsSummary
                = productItemService.getAvailableItemsSummary(product);

        /* Если нет доступных для покупки товаров,
        то в соответствии с требованиями нужно отображать цену,
        но не указывать размеры.
         */
        if (availableItemsSummary.isEmpty()) {
            BigDecimal currentPrice = product.getProductItems()
                    .get(0)
                    .getCurrentPrice();

            view.setCurrentPrice(Utils.prettyRoundToTens(currentPrice));

            if (product.getRrpPrice() != null) {
                view.setSavings(savings(product.getRrpPrice(), currentPrice));
            }

            return Optional.of(view);
        }
        else {

            view.setSizes(availableItemsSummary);

            view.setCurrentPrice(inferCurrentPrice(availableItemsSummary, view, product, nullableUser));
            view.setStartPrice(inferStartPrice(availableItemsSummary));

            BigDecimal lowestPrice              = getLowestPrice(availableItemsSummary);
            BigDecimal startPriceForLowestPrice = getStartPriceForLowestPrice(availableItemsSummary);
            if (!view.isHasDiscount()) {
                view.setHasDiscount(
                        Optional.ofNullable(lowestPrice)
                                .map(lp -> lp.compareTo(startPriceForLowestPrice) < 0)
                                .orElse(false)
                );
            }

            if (startPriceForLowestPrice.compareTo(lowestPrice) > 0) {
                view.setHasDecreasedPrice(true);
            }

            populateWithOffer(view, product, nullableUser);
        }

        return Optional.of(view);
    }

    /** Добавить информацию, относящуюся к области торга с продавцом товара. */
    private void populateWithOffer(ProductView view, Product product, User nullableUser) {

        Optional<Negotiability> negotiabilityIfAny = checkNegotiability(nullableUser, product);
        /* товар не поддерживает торг */
        if (!negotiabilityIfAny.isPresent()) { return; }

        List<Offer> offersHistory = offerRepository.findByProductAndOfferor(product, nullableUser,
                new Sort(Sort.Direction.DESC, "createdAt"));
        OfferRelated offerRelated = new OfferRelated()
                .setOffersHistory(offersHistory.stream().map(this::of).collect(Collectors.toList()));

        boolean productHasNegotiatedPrice = !offersHistory.isEmpty() && Boolean.TRUE.equals(offersHistory.get(0).getIsAccepted());
        if (productHasNegotiatedPrice) {
            String humanFriendlyNegotiatedPrice = Utils.formatPrice(offersHistory.get(0).getPrice());
            offerRelated.setNegotiatedPrice(humanFriendlyNegotiatedPrice)
                    .setNegotiationControlText("Продавец принял ваше предложение");
            view.setHasDiscount(true);
        }

        if (offersHistory.isEmpty()) {
            offerRelated.setNegotiationControlText("Предложить свою цену");
        }
        else {
            if (Boolean.TRUE.equals(offersHistory.get(0).getIsAccepted()))  {
                offerRelated.setNegotiationControlText("Продавец принял ваше предложение");
            }
            else if (offersHistory.get(0).getIsAccepted() == null)  {
                offerRelated.setNegotiationControlText("Ожидаем ответа продавца");
            }
            else if (offersHistory.size() == 3) {
                offerRelated.setNegotiationControlText("Попытки торга закончились");
            }
            else {
                offerRelated.setNegotiationControlText("Продавец отклонил предложение");
            }
        }

        Negotiability negotiability = negotiabilityIfAny.get();

        offerRelated.setAllowsNegotiation(negotiability.isPossible)
                .setReasonIfNotNegotiable(negotiability.reasonIfNot);

        view.setOfferRelated(offerRelated);
    }

    private ProductView.SingleOffer of(Offer o) {

        String formattedDate = o.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss O"));

        return new ProductView.SingleOffer()
                .setPrice(Utils.formatPrice(o.getPrice()))
                .setAccepted(o.getIsAccepted())
                .setAcceptedMessage(o.getIsAccepted() == null ? "Ожидаем ответа продавца" : o.getIsAccepted() ? "Продавец подтвердил предложение" : "Продавец отклонил предложение")
                .setOfferedAt(formattedDate)
                .setZonedOfferedAt(o.getCreatedAt());
    }

    @Override
    @Transactional
    public void updateState(Long productId, ProductState state) {

        Product product = productRepository.findOne(productId);
        if (product == null) { return; }

        UpdateStateStrategy.of(product, state)
                .execute(product, publisher, state);
    }

    @Override
    public List<ProductCondition> getActualConditions(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getActualConditions(categories) : productRepository.getActualConditions();
    }

    @Override
    public List<Brand> getActualBrands(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getActualBrands(categories) : productRepository.getActualBrands();
    }

    @Override
    public boolean getVintageProductsPresence(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getVintageProductsPresence(categories)
                : productRepository.getVintageProductsPresence();
    }

    @Override
    public boolean getNewCollectionProductsPresence(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getNewCollectionProductsPresence(categories)
                : productRepository.getNewCollectionProductsPresence();
    }

    @Override
    public boolean getProductsWithOurChoicePresence(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getProductsWithOurChoicePresence(categories)
                : productRepository.getProductsWithOurChoicePresence();
    }

    @Override
    public boolean getSaleProductsPresence(List<Long> categories) {
        return !categories.isEmpty()? productRepository.getSaleProductsPresence(categories)
                : productRepository.getSaleProductsPresence();
    }

    @Override
    public boolean canUserEditProduct(Long id, User nullableUser) {
        Optional<DetailedProduct> detailedProduct = this.getProduct(id);

        if (!detailedProduct.isPresent() || nullableUser == null) {
            return false;
        }

        if (!productStateAllowEdit(detailedProduct.get().getProduct())) { return false; }

        //PRO-пользователь не может тут править товар... пока что
        if (nullableUser.isPro()) return false;

        ProductItem productItem = detailedProduct.get().getProductItems().get(0);
        ProductItem.State itemState = productItem.getState();

        Long userId = nullableUser.getId();
        Long sellerId = detailedProduct.get().getProduct().getSeller().getId();

        return sellerId.equals(userId) && itemState.equals(ProductItem.State.INITIAL);

    }

    @Override
    @Transactional
    public void delete(Product product) {
        if (product.getProductState() == ProductState.DRAFT) {
            productRepository.delete(product);
        } else {
            product.setProductState(ProductState.DELETED);
        }
    }

    @Override
    public Map<Attribute, List<AttributeValue>> getActualAttributeValues(Long categoryId) {
        Optional<Category> interestingCategory = categoryService.findOne(categoryId);
        if (!interestingCategory.isPresent()) { return Collections.emptyMap(); }

        List<Long> actualAttributeValuesIds
                = productAttributeValueBindingRepository.getActualAttributeValues(interestingCategory.get());
        List<AttributeValue> actualAttributeValues = attributeValueRepository.findAll(actualAttributeValuesIds);

        return actualAttributeValues.stream()
                .collect(Collectors.groupingBy(AttributeValue::getAttribute));
    }

    @Override
    public List<CatalogSize> getActualSizes(Long categoryId) {
        Optional<Category> interestingCategory = categoryService.findOne(categoryId);
        if (!interestingCategory.isPresent()) { return Collections.emptyList(); }

        List<Long> actualSizesIds = productItemRepository.getActualSizes(interestingCategory.get());
        List<Size> actualSizes = sizeRepository.findAll(actualSizesIds);

        return cookSizes(actualSizes);
    }

    @Override
    public List<ProductCard> findProducts(String query, int limit) {

        String normalizedQuery = query.replace("  ", " ").trim();
        List<String> words = Arrays.asList(normalizedQuery.split(" "));
        List<String> queryWords = words.stream().map(w -> w + ":*").collect(Collectors.toList());

        List<Product> products = productRepository.fullTextSearch(String.join(" | ", queryWords),limit);

        return cookProductCards(products, Collections.emptyList(), null, null, false);
    }

    @Override
    public List<FullTextSearchProductView> findProductsByFullTextSearch(String query, int limit) {
        //Fix "&" - заменяем их все, затем ЛЮБОЕ кол-во пробелов заменяем на один пробел, далее по ним сплитуем на лексеммы
        String normalizedQuery = query.replaceAll("(\\s+&\\s+|\\s|:|\')"," ").trim();
        List<String> words = Arrays.asList(normalizedQuery.split(" "));
        List<String> queryWords = words.stream().map(w -> w.replace("'", "") + ":*").collect(Collectors.toList());
        List<Product> products = productRepository.fullTextSearch(String.join(" | ", queryWords), limit);
        return products.stream().map(p -> {
            List<ItemsSummaryBySize> availableItemsSummary
                    = productItemService.getAvailableItemsSummary(p, null);
            Optional<ProductImage> primaryImage = imageService.getPrimaryImage(p);
            return new FullTextSearchProductView(
                    "products",
                    primaryImage.map(ProductImage::getUrl).orElse(null),
                    p.getBrand().getName(),
                    null,
                    p.getCategory().getDisplayName(),
                    getLowestPrice(availableItemsSummary),
                    "/products/" + p.getId()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProductCard> findSomeBySeller(User seller, int limit) {

        List<Product> sellerProducts = productRepository.getAvailableProductsBySeller(seller)
                .stream()
                .limit(limit).collect(Collectors.toList());

        return cookProductCards(sellerProducts,Collections.emptyList(),null,null, false);

    }

    @Override
    public Integer countBySeller(User user) {
        return productRepository.countProductBySellerAndProductState(user, ProductState.PUBLISHED);
    }

    @Override
    public Optional<ProductCondition> getNewCondition() {
        return productConditionRepository.findByName("С биркой");
    }

    @Override
    public List<Size> getSameProductsSizeChart(Long productId) {
        Product product =  productRepository.findOne(productId);
        Category cat = product.getCategory();
        List<Category> parentCategoriesAndSelf = categoryRepository.findAllParents(cat.getId());
        parentCategoriesAndSelf.add(categoryRepository.findOne(cat.getId()));
       return sizeRepository.findSizes(parentCategoriesAndSelf);

    }

    @Override
    public int getLastWeekPublishedProductsCount() {

        LocalDateTime oneWeekAgoMidnight = LocalDateTime.now().minusWeeks(1).truncatedTo(DAYS);

        return productRepository.countByProductStateAndPublishTimeAfter(
                ProductState.PUBLISHED, oneWeekAgoMidnight);
    }

    @Override
    @Transactional
    public void makeAnOffer(Long id, BigDecimal newPrice, User user)
            throws PriceNegotiationException, NotFoundException {

        if (user == null) { throw new IllegalStateException("Пользователь не задан"); }
        if (newPrice == null) { throw new IllegalStateException("Новая цена не задана"); }

        Product product = productRepository.findOne(id);
        if (product == null) {
            throw new NotFoundException("Товар с идентификатором: " + id + "не существует");
        }

        Negotiability negotiability = checkNegotiability(user, product)
                      .orElseThrow(() -> new PriceNegotiationException("Торг для этого товара невозможен"));

        if (!negotiability.isPossible) {
            throw new PriceNegotiationException(negotiability.reasonIfNot);
        }

        /* свистопляска с округлениями, логика может оказаться неправильной */
        BigDecimal currentPrice = productRepository.getItemsSinglePriceIfAny(product)
                .map(p -> p.setScale(2, RoundingMode.UP))
                .orElseThrow(() -> new PriceNegotiationException("Торг для этого товара невозможен"));

        BigDecimal lowestPriceExcluded  = currentPrice.multiply(BigDecimal.valueOf(0.7),
                new MathContext(2, RoundingMode.UP));

        if (newPrice.compareTo(currentPrice) > -1) {
            throw new PriceNegotiationException(String.format("Цена должна быть между %s руб. и %s руб.",
                    Utils.formatPrice(lowestPriceExcluded), Utils.formatPrice(currentPrice)));
        }
        else if (newPrice.compareTo(lowestPriceExcluded) < 1) {
            throw new PriceNegotiationException("Цена должна быть выше 70% от начальной цены товара");
        }
        else {
            Offer o = new Offer().setCreatedAt(ZonedDateTime.now())
                    .setOfferor(user)
                    .setPrice(newPrice);

            product.addOffer(o);

            /* нужен идентификатор офера для того,
            * чтобы продавец, открыв уведомление, мог попасть из него на страницу подтверждения оффера */
            Offer newOffer = offerRepository.save(o);

            Context ctx = new Context();
            ctx.setVariable("resource", String.format("%s/account/offers/%d", appHost, newOffer.getId()));

            Product negotiatedProduct = newOffer.getProduct();
            String  productName = String.format("%s %s", negotiatedProduct.getDisplayName(),
                    negotiatedProduct.getBrand().getName());
            ctx.setVariable("productName", productName);
            ctx.setVariable("offeredPrice", Utils.formatPrice(newOffer.getPrice()));

            String messageContent = templateEngine.process("mail/offer-confirmation", ctx);
            String subject = String.format("Новое предложение по снижению цены товара %d", newOffer.getProduct().getId());

            sender.send(
                    product.getSeller().getEmail(),
                    subject,
                    messageContent
            );

            publisher.publishEvent(new NewOfferNotification()
                    .setOffer(o)
                    .setUser(product.getSeller()));
        }
    }

    @Override
    public Optional<BigDecimal> getNegotiatedPrice(Product product, User u) {

        return offerRepository.findOneByProductAndOfferorAndIsAcceptedIsTrue(product, u)
                .map(Offer::getPrice);
    }

    @Override
    @Transactional
    public void confirmOffer(Long offerId, User seller, boolean doConfirmOffer) {
        Offer validOffer = offerRepository.findByIdAndProductSeller(offerId, seller).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Предложение %s не найдено для продавца %s",
                                offerId, seller.getEmail()))
        );

        if (validOffer.getIsAccepted() != null) {
            String message = String.format("Предложение %d уже %s", offerId,
                    validOffer.getIsAccepted().equals(Boolean.TRUE) ? " принято" : " отклонено"
            );

            throw new IllegalArgumentException(message);
        }

        validOffer.setIsAccepted(doConfirmOffer);

        Context ctx = new Context();
        ctx.setVariable("resource",
                String.format("%s/products/%d", appHost, validOffer.getProduct().getId()));
        ctx.setVariable("resolvedOffer", validOffer);

        Product p = validOffer.getProduct();
        ctx.setVariable("productName", String.format("%s %s", p.getDisplayName(), p.getBrand().getName()));

        String messageContent = templateEngine.process("mail/seller-response-on-offer", ctx);
        String subject = String.format("Продавец ответил на ваше предложение по снижению цены товара %d", validOffer.getProduct().getId());

        sender.send(
                validOffer.getOfferor().getEmail(),
                subject,
                messageContent);

        publisher.publishEvent(doConfirmOffer?
                new OfferAcceptedNotification().setProduct(p).setUser(validOffer.getOfferor())
                : new OfferRejectedNotification().setProduct(p).setUser(validOffer.getOfferor()));
    }

    @Override
    public Optional<OfferView> getProductOffer(long offerId, User seller) {
        Offer validOffer = offerRepository.findByIdAndProductSeller(offerId, seller).orElse(null);
        if (validOffer == null) { return Optional.empty(); }

        return Optional.of(cookOfferView(validOffer));

    }

    @Override
    public int getAvailableProductsCount(User seller) {
        return productRepository.countSellerProducts(
                ProductState.PUBLISHED, ProductItem.State.INITIAL, seller);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.getAvailableProducts();
    }

    @Override
    public Optional<ProductImage> getPrimaryImage(Product p) { return imageService.getPrimaryImage(p); }

    @Override
    public Optional<BigDecimal> getItemsSinglePriceIfAny(Product p) {
        return productRepository.getItemsSinglePriceIfAny(p); }

    @Override
    public boolean productIsAvailable(Product p) {

        BooleanExpression availableProductItemsForGivenProduct =
                          productItem.product.eq(p)
                          .and(productItem.deleteTime.isNull()
                          .and(productItem.state.eq(ProductItem.State.INITIAL)));

        long count = productItemRepository.count(availableProductItemsForGivenProduct);
        return p.getProductState() == ProductState.PUBLISHED && count > 0;
    }

    private boolean productStateAllowEdit(Product p){
        BooleanExpression availableProductItemsForGivenProduct =
                productItem.product.eq(p)
                        .and(productItem.deleteTime.isNull()
                                .and(productItem.state.eq(ProductItem.State.INITIAL)));

        long count = productItemRepository.count(availableProductItemsForGivenProduct);

        return ((p.getProductState() == ProductState.PUBLISHED)||(p.getProductState() == ProductState.NEED_MODERATION))
                && count > 0;

    }

    private OfferView cookOfferView(Offer offer) {

		Product negotiatedProduct = offer.getProduct();

        OfferView offerView = new OfferView()
		        .setId(offer.getId())
				.setBrand(negotiatedProduct.getBrand().getName())
				.setCategory(negotiatedProduct.getCategory().getNameForProduct())
				.setCondition(negotiatedProduct.getProductCondition().getName());

		/* Изображения */
		imageService.getPrimaryImage(negotiatedProduct).ifPresent(offerView::setPrimaryImage);

		List<ProductImage> additionalImages = imageService.getProductImages(negotiatedProduct).stream()
				.filter((i) -> !i.isPrimary())
				.collect(Collectors.toList());
		offerView.setAdditionalImages(additionalImages);


		/* Атрибуты */
		Map<String, String> attributes = new HashMap<>();
		List<AttributeValue> productAttributeValues
				= productAttributeValueBindingRepository
				.findAttributeValuesByProductWithLimit(negotiatedProduct, 100);

		for (AttributeValue attributeValue : productAttributeValues) {
			attributes.put(
					attributeValue.getAttribute().getName(),
					attributeValue.getValue());
		}
        attributes.put("Описание", negotiatedProduct.getDescription());
		attributes.put("Артикул", negotiatedProduct.getVendorCode() != null?
				negotiatedProduct.getVendorCode()
				: "Не указан");

		offerView.setAttributes(attributes);

        try {
            BigDecimal offeredPriceWithoutCommission = commissionService.calculatePriceWithoutCommission(
                    offer.getPrice(),
                    negotiatedProduct.getSeller(),
                    negotiatedProduct.getCategory(),
                    negotiatedProduct.isTurbo(),
                    negotiatedProduct.isNewCollection()
            );

            BigDecimal currentPriceWithCommission = productRepository.getItemsSinglePriceIfAny(negotiatedProduct).orElseThrow(
                    /* FIXME обрабатывать без исключенеия: продавец открыл старое предложение по товару, который он уже продал */
                    () -> new IllegalStateException("Товар недоступен для ведения торга. Предложение: " + offer.getId())
            );

            offerView
                    .setCurrentPrice(Utils.formatPrice(currentPriceWithCommission.setScale(2, RoundingMode.DOWN)))
                    .setOfferedPrice(Utils.formatPrice(offer.getPrice().setScale(2, RoundingMode.DOWN)))
                    .setOfferedPriceWithoutCommission(
                        Utils.formatPrice(offeredPriceWithoutCommission.setScale(2, RoundingMode.DOWN)));

            offerView.setIsAccepted(offer.getIsAccepted());

            return offerView;

        } catch (CommissionException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("Предложение: " + offer.getId());
        }
    }

    private BooleanBuilder cookFilterExpression(FilterSpecification spec) {

        QProduct product = QProduct.product;
        QProductAttributeValueBinding productAttributeValue
                = QProductAttributeValueBinding.productAttributeValueBinding;

        BooleanBuilder filterExpression = new BooleanBuilder();

        /* Выбрать товары в заданных категориях */
        if (!spec.categoriesIds().isEmpty()) {
            filterExpression.and(product.category.id.in(spec.categoriesIds()));
        }

        /* Выбрать товары с заданным состоянием */
        if (spec.state() != null) {
            filterExpression.and(product.productState.eq(spec.state()));
        }

        /* Выбрать товары с заданными значениями атрибутов */
        if (!spec.interestingAttributeValues().isEmpty()) {
            List<AttributeValue> attributeValues
                    = attributeValueRepository.findAll(spec.interestingAttributeValues());

            /* Группировка конкретных значений атрибутов по атрибутам
            *  { Атрибут<Цвет>: [ЗначениеАтрибута<Красный>, ЗначениеАтрибута<Желтый>],
            *    Атрибут<Стиль>: [ЗначениеАтрибута<Повседневный>, ЗначениеАтрибута<Спортивный>],
            *    ...
            *  }
            **/
            Map<Attribute, List<AttributeValue>> attributeValuesByAttributes
                    = attributeValues.stream().collect(Collectors.groupingBy(AttributeValue::getAttribute));

            attributeValuesByAttributes.forEach((attr, values) -> {
                filterExpression.and(product.in(JPAExpressions
                        .selectDistinct(productAttributeValue.product).from(productAttributeValue)
                        .where(productAttributeValue.attributeValue.in(values))
                ));
            });
        }

        /* Выбрать товары заданных размеров */
        if (!spec.interestingSizes().isEmpty()) {
            filterExpression.and(productItem.size.id.in(spec.interestingSizes()));
        }

        /* Выбрать товары заданных брендов */
        if (!spec.interestingBrands().isEmpty()) {
            filterExpression.and(product.brand.id.in(spec.interestingBrands()));
        }

        /* Фильтр по продавцу товара */
        if(spec.sellerId() != null) {
            filterExpression.and(product.seller.id.eq(spec.sellerId()));
        }

        if(spec.isPro() != null) {
            if(spec.isPro()) {
                filterExpression.and(product.seller.proStatusTime.isNotNull());
            }
            else {
                filterExpression.and(product.seller.proStatusTime.isNull());
            }
        }

        if(spec.sellerEmailSubstring() != null) {
            filterExpression.and(product.seller.email.like("%" + spec.sellerEmailSubstring() + "%"));
        }


        if(spec.isDescriptionExists() != null) {
            if(spec.isDescriptionExists()) {
                filterExpression.and(product.description.isNotNull());
                filterExpression.and(product.description.isNotEmpty());
            }
            else {
                filterExpression.and(product.description.isEmpty().or(product.description.isNull()));
            }
        }

        if(spec.isVip() != null) {
            if(spec.isVip()) {
                filterExpression.and(product.seller.vipStatusTime.isNotNull());
            }
            else {
                filterExpression.and(product.seller.vipStatusTime.isNull());
            }
        }

        if (spec.itemState() != null) {
            filterExpression.and(productItem.state.eq(spec.itemState()));
            filterExpression.and(productItem.deleteTime.isNull());
        }

        if (!spec.interestingConditions().isEmpty()) {
            filterExpression.and(product.productCondition.id.in(spec.interestingConditions()));
        }

        if (spec.startPrice() != null) {
            filterExpression.and(productItem.currentPriceWithoutCommission.between(spec.startPrice(), null));
            filterExpression.and(productItem.deleteTime.isNull());
        }

        if (spec.endPrice() != null) {
            filterExpression.and(productItem.currentPriceWithoutCommission.between(null, spec.endPrice()));
            filterExpression.and(productItem.deleteTime.isNull());
        }

        if (Boolean.TRUE.equals(spec.isVintage())) {
            filterExpression.and(product.vintage.isTrue());
        }

        if (Boolean.TRUE.equals(spec.isOnSale())) {
            filterExpression.and(productItem.currentPrice.lt(productItem.startPrice));
        }

        if (Boolean.TRUE.equals(spec.hasOurChoice())) {
            filterExpression.and(product.ourChoiceStatusTime.isNotNull());
        }

        if(Boolean.TRUE.equals(spec.isNewCollection())) {
            filterExpression.and(product.isNewCollection.isTrue());
        }

        return filterExpression;
    }

    /** выборка товаров в выбранных категориях (с заданным state) по основным фильтрам за исключением одного из условий, переданных в параметре**/
    private BooleanBuilder cookFilterExpressionExceptOne(FilterSpecification spec, Map<Attribute, List<AttributeValue>> attributeValuesMap, boolean exceptSizes, boolean exceptBrands, boolean exceptConditions, Attribute exceptThisAttribute, boolean exceptIsVintage, boolean exceptIsOnSale, boolean exceptHasOurChoice, boolean exceptIsNewCollection) {
        QProduct product = QProduct.product;
        QProductAttributeValueBinding productAttributeValue
                = QProductAttributeValueBinding.productAttributeValueBinding;

        BooleanBuilder fullExpression = new BooleanBuilder();

        /* Выбрать товары в заданных категориях */
        if (!spec.categoriesIds().isEmpty()) {
            fullExpression.and(product.category.id.in(spec.categoriesIds()));
        }

        /* Выбрать товары с заданным состоянием */
        if (spec.state() != null) {
            fullExpression.and(product.productState.eq(spec.state()));
        }

        /* Item state тоже учитывается */
        if (spec.itemState() != null) {
            fullExpression.and(productItem.state.eq(spec.itemState()));
            fullExpression.and(productItem.deleteTime.isNull());
        }

        /* Выбрать товары заданных размеров */
        if (!spec.interestingSizes().isEmpty() && !exceptSizes) {
            fullExpression.and(productItem.size.id.in(spec.interestingSizes()));
        }

        /* Выбрать товары заданных брендов */
        if (!spec.interestingBrands().isEmpty() && !exceptBrands) {
            fullExpression.and(product.brand.id.in(spec.interestingBrands()));
        }

        /* Выбрать товары заданных состояний */
        if (!spec.interestingConditions().isEmpty() && !exceptConditions) {
            fullExpression.and(product.productCondition.id.in(spec.interestingConditions()));
        }

        /* Выбрать товары с заданными значениями атрибутов */
        if (!spec.interestingAttributeValues().isEmpty()) {
            attributeValuesMap.forEach((attribute, attributeValues)->{
                if(attribute!=exceptThisAttribute && !attributeValues.isEmpty()) {
                    BooleanExpression attributeValuesExp = product.in(JPAExpressions
                            .selectDistinct(productAttributeValue.product).from(productAttributeValue)
                            .where(productAttributeValue.attributeValue.in(attributeValues)));
                    fullExpression.and(attributeValuesExp);
                }
            });
        }

        /* Выбрать товары с заданными тегом vintage */
        if (Boolean.TRUE.equals(spec.isVintage()) && !exceptIsVintage) {
            fullExpression.and(product.vintage.isTrue());
        }

        /* Выбрать товары с заданными тегом OnSale */
        if (Boolean.TRUE.equals(spec.isOnSale()) && !exceptIsOnSale) {
            fullExpression.and(productItem.currentPrice.lt(productItem.startPrice));
        }

        /* Выбрать товары с заданными тегом OurChoice */
        if (Boolean.TRUE.equals(spec.hasOurChoice()) && !exceptHasOurChoice) {
            fullExpression.and(product.ourChoiceStatusTime.isNotNull());
        }

        /* Выбрать товары с заданными тегом NewCollection */
        if(Boolean.TRUE.equals(spec.isNewCollection()) && !exceptIsNewCollection) {
            fullExpression.and(product.isNewCollection.isTrue());
        }

        return fullExpression;
    }

    private Sort of(SortAttribute sortAttribute) {


        QSort actualSort;
        QProduct product = QProduct.product;

        switch (sortAttribute) {
            case ID:
                actualSort = new QSort(product.id.asc());
                break;

            case ID_DESC:
                actualSort = new QSort(product.id.desc());
                break;

            case PRICE:
                /* Если у сортируемых строк одно и то же значение атрибута сортировки, то они могут выводиться в произвольном порядке.
                * Их порядок будет различаться в разных запросах. Одна и та же строка может одновременно быть на разных страницах пагинации.
                * Поэтому если у строк совпадает значение атрибута сортировки, дополнительно сортировать их по идентификатору товара в порядке убывания
                * значения идентификатора. Тогда разные запросы будут возвращать строки в одном и том же порядке.*/
                actualSort = new QSort(productItem.startPrice.min().asc())
                            .and(new QSort(product.id.desc()));
                break;

            case PRICE_DESC:
                actualSort = new QSort(productItem.startPrice.min().desc())
                            .and(new QSort(product.id.desc()));
                break;

            case PUBLISH_TIME:
                actualSort = new QSort(product.publishTime.asc())
                            .and(new QSort(product.id.desc()));
                break;

            case PUBLISH_TIME_DESC:
                actualSort = new QSort(product.publishTime.desc())
                            .and(new QSort(product.id.desc()));
                break;

            default:
                actualSort = new QSort(product.publishTime.desc())
                            .and(new QSort(product.id.desc()));
        }

        return actualSort;
    }

    private CatalogProductPage cookProductsPage(Page<Product> rawProducts, List<Long> interestingSizes,
                                                User forNullableUser, SizeType nullableSizeType, boolean withSavings) {
        List<CatalogProduct> cookedProducts = new ArrayList<>();

        for (Product rawProduct : rawProducts.getContent()) {
            try {
                CatalogProduct catalogProduct = cookProduct(interestingSizes, forNullableUser, nullableSizeType, rawProduct, withSavings);
                cookedProducts.add(catalogProduct);
            }
            catch (Exception e) {
                throw new RuntimeException("Ошибка получения представления продукта. ProductId: " + rawProduct.getId(), e);
            }
        }

        return new CatalogProductPage(
                cookedProducts,
                rawProducts.getTotalPages(),
                rawProducts.getTotalElements()
        );
    }

    private CatalogProduct cookProduct(List<Long> interestingSizes, User forNullableUser, SizeType nullableSizeType, Product rawProduct, boolean withSavings) {
        List<ProductStatus> statuses
				= productStatusBindingRepository.findByProduct(rawProduct);

        Optional<ProductImage> primaryImage = imageService.getPrimaryImage(rawProduct);
        ProductItem productItem = productItemService.findFirstByProduct(rawProduct);

        List<ProductItem> items =
				productItemService.getItemsWithLowestPrice(rawProduct, interestingSizes);

        List<ItemsSummaryBySize> availableItemsSummary
				= productItemService.getAvailableItemsSummary(rawProduct, interestingSizes);

        BigDecimal lowestPrice = getLowestPrice(availableItemsSummary);
        BigDecimal startPriceForLowestPrice = getStartPriceForLowestPrice(availableItemsSummary);

        CatalogProduct catalogProduct = new CatalogProduct(
				rawProduct,
				primaryImage.orElse(null),
				statuses,
				productItem,
				items,
				lowestPrice,
                startPriceForLowestPrice);

        if (forNullableUser != null && rawProduct.getSeller().equals(forNullableUser)) {
			catalogProduct.setPurchaseRequestedProductItems(
					rawProduct.getPurchaseRequestedProductItems()
			);
		}

		if (forNullableUser != null) {
            catalogProduct.setLiked(likeService.doesUserLike(rawProduct, forNullableUser));
        }

        if (rawProduct.getSizeType() != SizeType.NO_SIZE) {

			SizeType effectiveSizeType = nullableSizeType != null? nullableSizeType : rawProduct.getSizeType();
			List<String> availableSizes = availableItemsSummary.stream()
					.map(summary -> summary.getSize().getBySizeType(effectiveSizeType))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			if (availableSizes.size() > 0) {

				catalogProduct.setSizeSummary(
						new CatalogProduct.SizeSummary(availableSizes,
								effectiveSizeType.getAbbreviation()));
			}
		}

		catalogProduct.setLikesCount(likeService.countByLikeable(rawProduct));
        BigDecimal rrpPrice = rawProduct.getRrpPrice();
        if (rrpPrice != null && withSavings) {
            catalogProduct.setRrp(rrpPrice);
            catalogProduct.setSavingsValue(savings(rrpPrice, lowestPrice));
        }

        return catalogProduct;
    }

    /* Дополнять карточку товара информацией, которая относится
    * к пользователю, просматривающему список товаров. */
    private void personalize(ProductCard productCard, Product rawProduct, User viewingUser) {

        productCard.setLiked(likeService.doesUserLike(rawProduct, viewingUser));

        if (rawProduct.getSeller().equals(viewingUser)) {
            List<PurchaseRequestedItem> purchaseRequestedItems
                    = rawProduct.getPurchaseRequestedProductItems().stream()
                    .map(i -> new PurchaseRequestedItem(i.getId(), i.getEffectiveOrder().getId()))
                    .collect(Collectors.toList());

            productCard.setPurchaseRequestedItems(purchaseRequestedItems);
        }
    }

    private BigDecimal getLowestPrice(List<ItemsSummaryBySize> availableItemsSummary) {
        //TODO: для ситуаций a.getLowestPrice() == null возможно нужна обработка
        return availableItemsSummary.stream().filter(a -> a.getLowestPrice() != null)
                .min(Comparator.comparing(ItemsSummaryBySize::getLowestPrice))
                .map(ItemsSummaryBySize::getLowestPrice)
                .orElse(null);
    }

    private BigDecimal getStartPriceForLowestPrice(List<ItemsSummaryBySize> availableItemsSummary) {
        return availableItemsSummary.stream().filter(a -> a.getLowestPrice() != null)
            .min(Comparator.comparing(ItemsSummaryBySize::getLowestPrice))
            .map(ItemsSummaryBySize::getStartPrice)
            .orElse(null);
    }

    private BreadcrumbsView getBreadcrumbsView(Product product) {

        BreadcrumbsView breadcrumbsView = new BreadcrumbsView(product.getBrand(), product.getDisplayName());

        List<CatalogCategory> parentCategories = categoryService.getAllParentCategories(product.getCategory().getId());
        if (!parentCategories.isEmpty()) {
            for (CatalogCategory c : parentCategories) {
               breadcrumbsView.addCategory(c.getDisplayName(), "/catalog/" + c.getUrlName());
            }
        }

        return breadcrumbsView;
    }

    private List<DescriptionAttributeView> getAttributes(Product p) {

        List<AttributeValue> productAttributeValues
                = productAttributeValueBindingRepository
                .findAttributeValuesByProductWithLimit(p, 100);

        List<DescriptionAttributeView> attributeViews = productAttributeValues.stream()
                .map(attributeValue -> new DescriptionAttributeView(
                    attributeValue.getAttribute().getName(),
                    attributeValue.getValue())).collect(Collectors.toList());

        ProductCondition productCondition = p.getProductCondition();
        if (productCondition != null) {
            attributeViews.add(new DescriptionAttributeView("Состояние товара", productCondition.getName()));
        }

        return attributeViews;
    }

    private SellerView getSellerView(User user) {

        SellerView sellerView = new SellerView();
        sellerView.setId(user.getId());
        sellerView.setOriginalUser(user);
        sellerView.setNick(user.getNickname());
        //FIXME Заполнять через Юзера, когда данные будут
        sellerView.setCountry("Россия");
        //FIXME метод должен отдвать путь, и не надо думать о том, какой префикс поставить "${resources.images.urlPrefix}"
        sellerView.setPhoto((user.getAvatarPath() != null ? "/img/" + user.getAvatarPath() : null));
        sellerView.setSex(user.getSex());
        sellerView.setRegistered(user.getRegistrationTime());
        sellerView.setProState(user.isPro());
        //FixMe Заполниь реальными данными, когда будут
        sellerView.setProductsSold(150);
        sellerView.setSubscribers(followingService.getFollowersCount(user));
        sellerView.setSubscriptions(followingService.getFollowingsCount(user));
        sellerView.setLikes(likeService.countByUser(user));

        return sellerView;
    }

    private List<ProductImage> getAdditionalImages(Product p) {
        return imageService.getProductImages(p).stream()
                .filter((i) -> !i.isPrimary())
                .collect(Collectors.toList());
    }

    private List<CatalogSize> cookSizes(List<Size> sizes) {
        List<CatalogSize> cookedSizes = new ArrayList<>();

        for (SizeType sizeType : SizeType.values()) {
            List<SizeView> sizesOfGivenType = new ArrayList<>();

            for (Size size : sizes) {
                String sizebySizeType = size.getBySizeType(sizeType);

                if (sizebySizeType != null) {
                    try {
                        SizeView sizeView = new SizeView(size.getId(), size.getBySizeType(sizeType),
                                mapper.writeValueAsString(size.getChart()));
                        sizesOfGivenType.add(sizeView);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            if (!sizesOfGivenType.isEmpty()) {
                cookedSizes.add(new CatalogSize(sizeType, sizesOfGivenType));
            }
        }

        return cookedSizes;
    }

    private String inferCurrentPrice(List<ItemsSummaryBySize> availableItemsSummary,
                                     ProductView view,
                                     Product product, User nullableUser) {

        boolean allItemsWithSameSizeHaveSamePrice = availableItemsSummary.stream()
                .filter(ItemsSummaryBySize::isWithDistinctPrices)
                .count() == 0;

        boolean allItemsHaveSameLowestPrice = availableItemsSummary.stream()
                    .map(ItemsSummaryBySize::getLowestPrice)
                    .distinct()
                    .count() == 1;

        /* Цена товара, которая выводится на основе данных по ценам доступных размеров. */
        BigDecimal inferredCurrentPrice;
        boolean hasMorePrices = false;
        if (allItemsHaveSameLowestPrice && allItemsWithSameSizeHaveSamePrice) {
            inferredCurrentPrice = availableItemsSummary.get(0).getLowestPrice();
            if (nullableUser != null) {
                Optional<BigDecimal> negotiatedPrice = getNegotiatedPrice(
                        product, nullableUser);

                boolean negotiatedPriceIsValid = negotiatedPrice.isPresent()
                        && inferredCurrentPrice.compareTo(negotiatedPrice.get()) > -1;
                if (negotiatedPriceIsValid) {
                    inferredCurrentPrice = negotiatedPrice.get();
                    view.setHasDiscount(true);
                }
            }
        } else {
             inferredCurrentPrice = availableItemsSummary.stream()
                    .min(Comparator.comparing(ItemsSummaryBySize::getLowestPrice)).get()
                    .getLowestPrice();
             hasMorePrices = true;
        }

        String formattedPrice = Utils.prettyRoundToTens(inferredCurrentPrice);

        if (product.getRrpPrice() != null) {
            view.setSavings(savings(product.getRrpPrice(), inferredCurrentPrice));
        }

        return (hasMorePrices? "От " : "") + formattedPrice;
    }

    private String inferStartPrice(List<ItemsSummaryBySize> availableItemsSummary){
        BigDecimal startPrice = availableItemsSummary.stream()
                .max(Comparator.comparing(ItemsSummaryBySize::getStartPrice)).get()
                .getStartPrice();
        return Utils.prettyRoundToTens(startPrice);
    }

    /* Проверить, поддерживает ли данный товар возможность торга. */
    private Optional<Negotiability> checkNegotiability(User offeror, Product product) {

        boolean productAllowsNegotiation = product.getProductState() == ProductState.PUBLISHED
                && productRepository.hasAvailableItems(product);
        if (!productAllowsNegotiation) { return Optional.empty(); }

        /* Пока что торг возможен, только если у всех вещей товара одинаковая цена.
        Если у вещей цена разная, непонятно, как торг должен работать: по заданию его поведение в этом случае не определено. */
        Optional<BigDecimal> singlePriceIfAny = productRepository.getItemsSinglePriceIfAny(product);
        if (!singlePriceIfAny.isPresent()) { return Optional.empty(); }

        /* Аноним видит возможность торга, но при ее выборе должен переходить на страницу входа */
        if (offeror == null) { return Optional.of(new Negotiability(true, null)); }

        Long daysPastSinceProductPublication = DAYS.between(product.getPublishTime(), LocalDateTime.now());

        /* пользователь не может торговаться и вообще видеть возможность торга,
        если он не вип и не прошло 7 дней с момента публикации товара */
        if (!offeror.isVip() && daysPastSinceProductPublication.compareTo(8L) == -1 && !product.isTurbo()) {
            return Optional.empty();
        }

        List<Offer> offersHistory = offerRepository.findByProductAndOfferor(product, offeror,
                new Sort(Sort.Direction.DESC, "createdAt"));

        if (offersHistory.isEmpty()) { return Optional.of(new Negotiability(true, null)); }

        Offer lastOffer = offersHistory.get(0);
        /* Пользователь не может торговаться,
        если продавец еще не вынес решения по последнему предложению. */
        if (lastOffer.getIsAccepted() == null) {
            return Optional.of(new Negotiability(false,
                            "Продавец еще не подтвердил последнее предложение. Новое предложение запросить нельзя.")
            );
        }

        /* Пользователь не может торговаться,
        если продавец уже подтвердил последнее предложение по снижению цены.
        Пользователь в этому случае может только перейти к оплате заказа, который состоит только из этого товара.
        */
        if (lastOffer.getIsAccepted().equals(true)) {
            return Optional.of(new Negotiability(false,
                    "Продавец уже подтвердил последнее предложение. Новое предложение запросить нельзя."));
        }

        /* Если продавец отказался от последнего предложения, то пользователь может продолжить
        торговаться только в том случае, если у него еще есть доступные для этого попытки. */
        if (lastOffer.getIsAccepted().equals(false) && offersHistory.size() == 3 ) {
            return Optional.of(new Negotiability(false,
            "Продавец отверг последнее предложение. " +
                    "Запросить новое предложение нельзя: вы исчерпали доступные попытки."));
        }

        return Optional.of(new Negotiability(true, null));
    }

    private static int savings(BigDecimal rrp, BigDecimal currentPrice) {
        return rrp.subtract(currentPrice)
                .divide(rrp, 2, BigDecimal.ROUND_DOWN)
                .movePointRight(2).intValue();
    }

    @AllArgsConstructor
    private class Negotiability {

        /* Торг воможен в данный момент. */
        boolean isPossible;

        /* Причина, по которой торг невозможен в данный момент. */
        String reasonIfNot;
    }

    private enum UpdateStateStrategy {

        publish {
            @Override
            void execute(Product product, ApplicationEventPublisher pub, ProductState newState) {

                ProductState previousState = product.getProductState();

                product.publish();

                ProductPublishedNotification notification = new ProductPublishedNotification().setProduct(product);
                NotificationPackage<ProductPublishedNotification> notificationPackage = new NotificationPackage<>(product.getSeller(), notification);
                pub.publishEvent(notificationPackage);

                pub.publishEvent(new ProductPublishedNotification()
                        .setProduct(product)
                        .setUser(product.getSeller()));

                if (previousState == ProductState.NEED_MODERATION) {
                    pub.publishEvent(new ModerationPassedNotification()
                            .setProduct(product)
                            .setUser(product.getSeller()));
                }
            }
        },

        rejected_by_moderator {
            @Override
            void execute(Product p, ApplicationEventPublisher pub, ProductState state) {
                p.setProductState(state);
                pub.publishEvent(new ModerationFailedNotification()
                        .setProduct(p)
                        .setUser(p.getSeller()));
            }
        },

        hide {
            @Override
            void execute(Product p, ApplicationEventPublisher pub, ProductState state) {
                p.setProductState(state);
                pub.publishEvent(new ProductHiddenNotification()
                        .setProduct(p)
                        .setUser(p.getSeller()));
            }
        },

        by_default {
            @Override
            void execute(Product p, ApplicationEventPublisher pub, ProductState state) {
                p.setProductState(state);
            }
        };

        abstract void execute(Product p, ApplicationEventPublisher pub, ProductState state);

        static UpdateStateStrategy of(Product p, ProductState newState) {
            ProductState current = p.getProductState();

            return newState == ProductState.PUBLISHED ? publish

                    : (newState == ProductState.REJECTED && current == ProductState.NEED_MODERATION) ? rejected_by_moderator

                    : (newState == ProductState.HIDDEN) ? hide

                    : by_default;

        }
    }
}
