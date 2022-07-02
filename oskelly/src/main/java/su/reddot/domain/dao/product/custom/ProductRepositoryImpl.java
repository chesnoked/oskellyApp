package su.reddot.domain.dao.product.custom;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.data.querydsl.QPageRequest;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.QProduct;
import su.reddot.domain.model.product.QProductItem;

import java.util.Collections;
import java.util.List;

/**
 * Использует querydsl left join для построения запроса по связанным сущностям Product, ProductItem.
 */
public class ProductRepositoryImpl extends QueryDslRepositorySupport implements ProductRepositoryCustom {

    public ProductRepositoryImpl() { super(Product.class); }

    @Override
    public Page<Product> find(Predicate predicate, Pageable p) {

        QProduct product = QProduct.product;
        QProductItem productItem = QProductItem.productItem;

        JPAQuery<Product> productsWithTheirItems = new JPAQuery<Product>(super.getEntityManager()).
                from(product).leftJoin(product.productItems, productItem)
                .where(predicate)
                .groupBy(product.id);

        return readPage(productsWithTheirItems, p);
    }

    private Page<Product> readPage(JPAQuery<Product> query, Pageable page) {
        if (page == null) {
            return readPage(query, new QPageRequest(0, Integer.MAX_VALUE));
        }

        long total = query.clone(super.getEntityManager()).fetchCount();

        JPQLQuery pagedQuery = getQuerydsl().applyPagination(page, query);
        List<Product> content = total > page.getOffset() ? pagedQuery.fetch()
                : Collections.emptyList();

        return new PageImpl<>(content, page, total);
    }
}
