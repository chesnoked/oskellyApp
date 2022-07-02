package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductStatus;
import su.reddot.domain.model.product.ProductStatusBinding;

import java.util.List;
import java.util.Optional;

public interface ProductStatusBindingRepository extends JpaRepository<ProductStatusBinding, Long> {

    @Query("select p.productStatus " +
            "from ProductStatusBinding p " +
            "where p.product = ?1 " +
            "order by p.productStatus.id")
    List<ProductStatus> findByProduct(Product product);

    Optional<ProductStatusBinding> findByProductAndProductStatus(Product product, ProductStatus productStatus);

    void deleteByProductAndProductStatus(Product product, ProductStatus productStatus);
}
