package su.reddot.infrastructure.export.vk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

interface ExportedProductRepository
        extends JpaRepository<ExportedProduct, Long>,
        QueryDslPredicateExecutor<ExportedProduct> {

    /**
     * @return список товаров, которые перестали быть доступными
     * на сайте, но которые уже были экспортированы ранее во внешнюю систему.
     */
    @Query("select ep from ExportedProduct ep " +
            "where ep.product not in (" +
                "select p from Product p join p.productItems pi " +
                "where p.productState = 'PUBLISHED' " +
                "and pi.deleteTime is null and pi.state = 'INITIAL')")
    List<ExportedProduct> findObsolete();
}
