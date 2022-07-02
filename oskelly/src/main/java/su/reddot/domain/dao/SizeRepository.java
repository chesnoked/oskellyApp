package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.size.Size;

import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long> {

    /**
     * Получить размеры той категории, у которой эти размеры есть
     * и которая находится ниже остальных в иерархии.
     *
     * @param categories список категорий
     *
     * @return список размеров для категории, которая стоит ниже остальных
     *         переданных категорий.
     */
    @Query("select s from Size s left join s.category c" +
            " where c in ?1 and c.leftOrder = " +
            " (select max(c.leftOrder) from Size s left join s.category c where c in ?1)" +
            " order by s.id")
    List<Size> findSizes(List<Category> categories);
}
