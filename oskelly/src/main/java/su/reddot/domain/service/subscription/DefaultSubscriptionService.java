package su.reddot.domain.service.subscription;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.product.ProductConditionRepository;
import su.reddot.domain.dao.subscription.PriceUpdateSubscriptionRepository;
import su.reddot.domain.dao.subscription.ProductAlertSubscriptionRepository;
import su.reddot.domain.dao.subscription.ProductSubscriber;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.attribute.SimpleAttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.notification.ModerationPassedNotification;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.notification.PriceChangedNotification;
import su.reddot.domain.model.notification.SuitableProductAppeared;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.subscription.PriceUpdateSubscription;
import su.reddot.domain.model.subscription.ProductAlertAttributeValueBinding;
import su.reddot.domain.model.subscription.ProductAlertSubscription;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogAttribute;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.domain.service.publication.PriceChangedEvent;
import su.reddot.infrastructure.sender.NotificationSender;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static su.reddot.domain.model.product.QProductCondition.productCondition;
import static su.reddot.domain.model.subscription.QProductAlertSubscription.productAlertSubscription;

@Component
@RequiredArgsConstructor
public class DefaultSubscriptionService implements SubscriptionService {

    private final PriceUpdateSubscriptionRepository priceUpdateSubscriptionRepository;
    private final ProductAlertSubscriptionRepository productAlertSubscriptionRepository;

    private final BrandService              brandService;
    private final CategoryService           categoryService;

    private final SizeRepository sizeRepo;
    private final AttributeValueRepository valueRepo;
    private final ProductConditionRepository conditionRepository;

    private final ApplicationEventPublisher pub;
    private final TemplateEngine templateEngine;
    private final NotificationSender sender;

    @Value("${app.host}")
    private String host;


    @Override
    public Long countAllPriceSubscribers(Product product) {
        return priceUpdateSubscriptionRepository.countAllByProduct(product);
    }

    @Override
    public boolean isPriceSubscriptionExist(User subscriber, Product product) {
        return priceUpdateSubscriptionRepository.findBySubscriberAndProduct(subscriber, product).isPresent();
    }

    @Override
    public boolean subscribeOnPrice(User subscriber, Product product) {
        if (this.isPriceSubscriptionExist(subscriber, product)){ return false; }

        PriceUpdateSubscription sub = new PriceUpdateSubscription().setSubscriber(subscriber).setProduct(product);
        priceUpdateSubscriptionRepository.save(sub);
        return true;

    }

    @Override
    public boolean unsubscribeOnPrice(User subscriber, Product product) {
        Optional<PriceUpdateSubscription> sub = priceUpdateSubscriptionRepository.findBySubscriberAndProduct(subscriber, product);

        if(!sub.isPresent()){
            return false;
        }
        priceUpdateSubscriptionRepository.delete(sub.get().getId());
        return true;
    }

    @Override
    public boolean toggle(User subscriber, Product product) {

        if(!isPriceSubscriptionExist(subscriber, product)) {
            PriceUpdateSubscription sub = new PriceUpdateSubscription().setSubscriber(subscriber).setProduct(product);
            priceUpdateSubscriptionRepository.save(sub);
            return true;
        }
        else {
            priceUpdateSubscriptionRepository.findBySubscriberAndProduct(subscriber, product).ifPresent(s -> priceUpdateSubscriptionRepository.delete(s.getId()));
            return false;
        }
    }

    @Override
    public void createAlertSubscription(User subscriber,
                                           Brand brand,
                                           Size size,
                                           Category category,
                                           SizeType viewSizeType,
                                           ProductCondition condition,
                                           List<AttributeValue> attributeValues
    ) {
        ProductAlertSubscription sub = new ProductAlertSubscription()
            .setBrand(brand)
            .setCategory(category)
            .setSize(size)
            .setViewSizeType(viewSizeType)
            .setProductCondition(condition)
            .setSubscriber(subscriber)
            .setCreatedAt(ZonedDateTime.now());

        for (AttributeValue attributeValue : attributeValues) {
            ProductAlertAttributeValueBinding alertAttributeValueBinding = new ProductAlertAttributeValueBinding();
            alertAttributeValueBinding.setAttributeValue(attributeValue);
            sub.setAttributeValueBinding(alertAttributeValueBinding);
        }

        productAlertSubscriptionRepository.save(sub);
    }

    @Override
    public void createAlertSubscription(User subscriber, Long brandId, Long sizeId,
                                       Long catId, SizeType sizeType, Long conditionId, List<Long> attrValues) {

        boolean attrValuesAreDefined = attrValues != null && !attrValues.isEmpty();
        boolean noneOfAttributesAreDefined = brandId == null
                && (sizeId == null || sizeType == null)
                && catId == null
                && conditionId == null
                && !attrValuesAreDefined;

        if (noneOfAttributesAreDefined) {
            throw new IllegalArgumentException("Для создания уведомления нужно указать хотя бы один атрибут");
        }

        Brand brand = brandId == null? null : brandService.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Бренд с идентификатором " + brandId + " не существует"));

        Size size = sizeId == null? null : sizeRepo.findOne(sizeId);
        if (size == null && sizeId != null) {
            throw new IllegalArgumentException("Размер с идентификатором " + sizeId + " не существует");
        }

        ProductCondition condition = conditionId == null? null : conditionRepository.findOne(conditionId);
        if (condition == null && conditionId != null) {
            throw new IllegalArgumentException("Состояние товара с идентификатором " + conditionId + " не существует");
        }

        Category category = categoryService.findOne(catId)
                .orElseThrow(() -> new IllegalArgumentException("Категория с идентификатором " + catId + " не существует"));

        List<AttributeValue> values = Collections.emptyList();
        if (attrValuesAreDefined) {
            values = valueRepo.findAll(attrValues);
            if (values.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        createAlertSubscription(subscriber, brand, size, category, sizeType, condition, values);
    }

    @Override
    public List<ProductAlertSubscription> getProductAlertSubscriptions(User user) {

        BooleanExpression predicate = productAlertSubscription.subscriber.eq(user);

        Iterable<ProductAlertSubscription> found = productAlertSubscriptionRepository.findAll(
                predicate, productAlertSubscription.createdAt.desc());

        List<ProductAlertSubscription>  cooked = new ArrayList<>();
        found.forEach(cooked::add);

        return cooked;
    }

    @Override
    @EventListener
    public void populate(ProductModelEvent event) {

        Product product = event.getProduct();

        List<AlertTemplate.Brand> alertBrands = Stream.of(product.getBrand())
                .map(this::from)
                .collect(toList());

        Sort conditionSort = new QSort(productCondition.sortOrder.asc());
        List<AlertTemplate.Condition> alertConditions
                = conditionRepository.findAll(conditionSort).stream()
                .map(this::from)
                .collect(toList());

        Category category   = product.getCategory();
        Long     categoryId = category.getId();
        List<AlertTemplate.Attribute> alertAttributes
                = categoryService.getAllAttributes(categoryId).stream()
                .map(this::from)
                .collect(toList());

        List<CatalogSize> sizesGroupedBySizeType
                = categoryService.getSizesGroupedBySizeType(categoryId);
        boolean categoryHasSizes = !sizesGroupedBySizeType.isEmpty()
                && sizesGroupedBySizeType.get(0).getSizeType() != SizeType.NO_SIZE;

        AlertTemplate alertTemplate = new AlertTemplate()
                .setBrands(alertBrands)
                .setConditions(alertConditions)
                .setAttributes(alertAttributes)
                .setCategoryId(categoryId)
                .setCategoryName(category.getNameForProduct())
                .setParentCategories(parentCategories(categoryId))
                .setSizes(categoryHasSizes? sizesGroupedBySizeType : null);


        Map<String, Object> view = event.getView();
        view.put("alertTemplate", alertTemplate);
    }

    /** Оповестить пользователей о появлении нового товара.*/
    @Override
    @EventListener
    public void onNewProductAppearance(ModerationPassedNotification event) {
        Product newProduct = event.getProduct();
        
        List<ProductSubscriber> subscribers
                = productAlertSubscriptionRepository.subscribers(newProduct);

        for (ProductSubscriber productSubscriber : subscribers) {
            
            User subscriber = productSubscriber.getSubscriber();
            
            pub.publishEvent(new SuitableProductAppeared()
                    .setProduct(newProduct)
                    .setUser(subscriber)
            );

            send(subscriber.getEmail(), newProduct);
        }
    }

    /** Оповестить заинтересованных пользователей об изменении цены товара */
    @Override
    @Async @EventListener
    public void onPriceChange(PriceChangedEvent event) {
        Product p = event.getProductWithAlteredPrice();
        getAllPriceSubscribers(p)
                .forEach(sub -> notifyOfAlteredPrice(sub, p));
    }

    /**
     * @apiNote если указанная подписка не найдена, ничего не делать.
     */
    @Override
    public void removeSubscription(Long id, User user) {

        BooleanExpression predicate = productAlertSubscription.id.eq(id)
                          .and(productAlertSubscription.subscriber.eq(user));
        if (productAlertSubscriptionRepository.exists(predicate)) {
            productAlertSubscriptionRepository.delete(id);
        }
    }

    private List<String> parentCategories(Long categoryId) {
        return categoryService.getAllParentCategories(categoryId).stream()
                .map(CatalogCategory::getDisplayName)
                .collect(toList());
    }

    private AlertTemplate.Brand from(Brand b) {

        return new AlertTemplate.Brand()
                .setId(b.getId())
                .setName(b.getName());
    }

    private AlertTemplate.Condition from(ProductCondition c) {

        return new AlertTemplate.Condition()
                .setId(c.getId())
                .setName(c.getName());
    }

    private AlertTemplate.Attribute from(CatalogAttribute catalogAttribute) {

        List<SimpleAttributeValue> values = catalogAttribute.getValues();

        List<AlertTemplate.Attribute.Value> alertValues = values.stream()
                .map(v -> new AlertTemplate.Attribute.Value()
                        .setId(v.getId())
                        .setName(v.getValue()))
                .collect(toList());

        Attribute attr = catalogAttribute.getAttribute();

        return new AlertTemplate.Attribute()
                .setId(attr.getId())
                .setName(attr.getName())
                .setValues(alertValues);
    }

    private void send(String email, Product newProduct) {

        Context ctx = new Context();
        ctx.setVariable("product", newProduct);
        ctx.setVariable("host", host);

        String text = templateEngine.process("mail/alert-triggered", ctx);

        sender.send(email, "Новый товар, который вы отслеживаете", text);
    }

    private void notifyOfAlteredPrice(User recipient, Product productWithAlteredPrice) {

        Context ctx = new Context();
        ctx.setVariable("product", productWithAlteredPrice);
        ctx.setVariable("host", host);

        String text = templateEngine.process("mail/price-changed", ctx);

        sender.send(recipient.getEmail(), "Новая цена у товара, который вы отслеживаете", text);

        Notification priceChangedEvent = new PriceChangedNotification()
                .setProduct(productWithAlteredPrice)
                .setUser(recipient);

        pub.publishEvent(priceChangedEvent);

    }

    private List<User> getAllPriceSubscribers(Product product) {
        return priceUpdateSubscriptionRepository.getAllByProduct(product).stream()
                .map(PriceUpdateSubscription::getSubscriber)
                .collect(toList());
    }

}
