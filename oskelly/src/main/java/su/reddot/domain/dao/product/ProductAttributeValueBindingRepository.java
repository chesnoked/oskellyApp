package su.reddot.domain.dao.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductAttributeValueBinding;

import java.util.List;

public interface ProductAttributeValueBindingRepository extends JpaRepository<ProductAttributeValueBinding, Long> {

    @Query("select a.attributeValue from ProductAttributeValueBinding a where a.product = :product")
    List<AttributeValue> findAttributeValuesByProduct(@Param("product") Product product, Pageable pageable);

    default List<AttributeValue> findAttributeValuesByProductWithLimit(Product product, int limit) {
        return findAttributeValuesByProduct(product, new PageRequest(0, limit));
    }

    /**
     * <p>Получить 1) только те атрибуты, которые относятся в выбранной категории
     * 2) только те значения этих атрибутов, которыми обладают товары в выбранной категории.
     * Если товар относится к какой - то дочерней категории выбранной категории, и у этой дочерней категории
     * есть свои собственные атрибуты, то эти атрибуты не попадают в выборку.</p>
     * <p>Например, выбрали категорию "Женское". У этой категории есть собственный атрибут - "цвет".
     * У ее дочерних категорий "Женская одежда", "Женская обувь" есть свои атрибуты "Материал одежды" и
     * "Материал обуви" соответственно.
     * Пользователь на странице категории "Женское" увидит все женские товары, в том числе женскую обувь и одежду.
     * Но для фильтрации будет доступен только атрибут "Цвет". В этом фильтре будут доступны только те цвета,
     * которые есть у товаров в разделе "Женское".</p>
     *
     * @param c произвольная категория, не обязательно конечная.
     * @return список актуальных значений атрибутов
     */
    @Query("select distinct binding.attributeValue.id " +
           "from ProductAttributeValueBinding binding join binding.product.productItems items " +
           "where binding.product.category in " +

                "(select category from Category category where category.leftOrder >= :#{#c.leftOrder} and category.rightOrder <= :#{#c.rightOrder}) " +

            "and binding.attributeValue.attribute in " +

                "(select categoryAttribute.attribute " +
                "from CategoryAttributeBinding categoryAttribute " +
                "where categoryAttribute.category in " +

                    "(select category from Category category " +
                    "where category.leftOrder <= :#{#c.leftOrder} and category.rightOrder >= :#{#c.rightOrder})) " +

            "and binding.product.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
            "and (binding.product.brand = :#{#brand} or :#{#brand} is null)" +
            "and items.deleteTime is null " +
            "and items.state = 'INITIAL' " +
            "order by binding.attributeValue.id")
    List<Long> getActualAttributeValues(@Param("c") Category c, @Param("brand") Brand b);

    default List<Long> getActualAttributeValues(@Param("c") Category c) {
        return getActualAttributeValues(c, null);
    }

    void deleteByProduct(Product product);
}
