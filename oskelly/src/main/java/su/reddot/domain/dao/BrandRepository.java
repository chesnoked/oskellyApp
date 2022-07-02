package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("select b from Brand b where LOWER(name) like %:substr%")
    List<Brand> findBySubstring(@Param("substr") String substr);

    @Query("select b from Brand b where LOWER(name) = LOWER(:name)")
    Brand findFirstByName(@Param("name") String name);

    /** @return Первые буквы существующих брендов */
    @Query(value = "SELECT DISTINCT SUBSTR(b.name, 1, 1) as firstChar FROM brand b " +
            "RIGHT JOIN product p ON p.brand_id = b.id JOIN product_item pri ON pri.product_id = p.id " +
            "WHERE p.product_state = 'PUBLISHED'  AND pri.delete_time IS NULL  AND pri.state = 'INITIAL' " +
            "ORDER BY firstChar",
            nativeQuery = true)
    List<String> getDistinctCharsOfBrands();

    /** @return Группа брендов по первой букве
     * Берем только те бренды, что представленны в продаже
     */
    @Query(value = "SELECT DISTINCT b.* FROM brand b RIGHT JOIN product p ON p.brand_id = b.id " +
            "JOIN product_item pri ON pri.product_id = p.id " +
            "WHERE SUBSTR(b.name, 1, 1) = ?1  AND p.product_state = 'PUBLISHED' " +
            "AND pri.delete_time IS NULL  AND pri.state = 'INITIAL' ORDER BY b.name ASC",
            nativeQuery = true
    )
    List<Brand> findAllByFirstChar(String firstChar);

    Optional<Brand> findFirstByUrl(String url);
}
