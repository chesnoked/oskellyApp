package su.reddot.domain.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.product.Image;
import su.reddot.domain.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductOrderByPhotoOrderAsc(Product product, Pageable pageable);

    default List<Image> findByProductWithLimit(Product product, int limit) {
        Pageable pageable = new PageRequest(0, limit);
        return findByProductOrderByPhotoOrderAsc(product, pageable);
    }

    /**
     * Получить основное изображение товара
     * @param product товар
     * @return основное изображение
     */
    Optional<Image> findFirstByProductAndIsMainTrue(Product product);

    @Query("UPDATE Image p set p.isMain=false where p.product = :product")
    @Modifying
    void setPhotoNotPrimaryByProduct(@Param("product") Product product);

    Optional<Image> findFirstByProductAndPhotoOrder(Product product, int photoOrder);
}
