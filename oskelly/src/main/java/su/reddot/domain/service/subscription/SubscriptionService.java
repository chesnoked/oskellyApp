package su.reddot.domain.service.subscription;

import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.notification.ModerationPassedNotification;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.subscription.ProductAlertSubscription;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.domain.service.publication.PriceChangedEvent;

import java.util.List;

public interface SubscriptionService {

    Long countAllPriceSubscribers(Product product);

    boolean subscribeOnPrice(User subscriber, Product product);

    boolean unsubscribeOnPrice(User subscriber, Product product);

    boolean toggle(User subscriber, Product product);

    boolean isPriceSubscriptionExist(User subscriber, Product product);

    void createAlertSubscription(User subscriber,
                                    Brand brand,
                                    Size size,
                                    Category category,
                                    SizeType viewSizeType,
                                    ProductCondition condition,
                                    List<AttributeValue> attributeValues
    );

    void createAlertSubscription(User subscriber, Long brandId, Long sizeId,
                                 Long catId, SizeType sizeType, Long condition, List<Long> attributeValuesIds);

    /** Список подписок пользователя. */
    List<ProductAlertSubscription> getProductAlertSubscriptions(User user);

    /** Добавить в данные о товаре шаблон для создания подписки на товар. */
    @SuppressWarnings("unused") /* используется при возникновении соответствующего события */
    void populate(ProductModelEvent event);

    @SuppressWarnings("unused") /* используется для обработки соответствующего события */
    void onNewProductAppearance(ModerationPassedNotification event);

    @SuppressWarnings("unused") /* используется для обработки соответствующего события */
    void onPriceChange(PriceChangedEvent event);

    /** Удалить подписку для данного пользователя. */
    void removeSubscription(Long id, User user);
}
