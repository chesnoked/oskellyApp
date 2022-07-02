package su.reddot.infrastructure.export.vk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.product.Image;

import java.util.List;

interface ExportedImageRepository
        extends JpaRepository<ExportedImage, Long>,
        QueryDslPredicateExecutor<ExportedImage> {

    @Modifying @Query("delete from ExportedImage i where i.image in ?1")
    void deleteByImageIn(List<Image> images);
}
