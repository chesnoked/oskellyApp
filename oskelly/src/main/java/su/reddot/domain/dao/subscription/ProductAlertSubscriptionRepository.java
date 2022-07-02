package su.reddot.domain.dao.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.subscription.ProductAlertSubscription;

import java.util.List;

public interface ProductAlertSubscriptionRepository
        extends JpaRepository<ProductAlertSubscription, Long>,
        QueryDslPredicateExecutor<ProductAlertSubscription> {

    /**
     * Получить пользователей, подписки которых содержат параметры, совпадающие с новым товаром.
     */
    @Query("select distinct sub.subscriber as subscriber " +
            "from ProductAlertSubscription sub left join sub.attributeValueBindings subAttribute" +

            /* поиск подписок, категории в которых либо не заданы, либо совпадают
            или являются родительскими для категории опубликованного товара */
            " where (sub.category in (" +
            "   select cat from Category cat" +
            "   where cat.leftOrder <= :#{#newProduct.category.leftOrder}" +
            "   and cat.rightOrder >= :#{#newProduct.category.rightOrder})" +
            " or sub.category is null)" +

            /* поиск подписок, бренды в которых либо не заданы, либо совпадают с брендом товара */
            " and (sub.brand = :#{#newProduct.brand}" +
            " or sub.brand is null)" +

            /* аналогично для размеров ... */
            " and (sub.size in (" +
            "   select distinct pi.size from ProductItem pi" +
            "   where pi.product = :#{#newProduct})" +
            " or sub.size is null)" +

            /* ... и значений атрибутов ...*/
            " and (subAttribute.attributeValue in :#{#newProduct.attributeValues.![attributeValue]}" +
            " or subAttribute is null)" +

            /* ... и состояний товара. */
            " and (sub.productCondition = :#{#newProduct.productCondition}" +
            " or sub.productCondition is null)" +

            /* не учитывать продавца товара, если у того есть подписка,
            по атрибутам подходящая своему же новому товару. */
            " and sub.subscriber <> :#{#newProduct.seller}"

    )
    List<ProductSubscriber> subscribers(@Param("newProduct") Product newProduct);
}