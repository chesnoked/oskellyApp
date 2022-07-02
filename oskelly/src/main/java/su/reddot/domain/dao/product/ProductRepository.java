package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.dao.product.custom.ProductRepositoryCustom;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom, QueryDslPredicateExecutor {

    List<Product> findProductsBySellerIdAndProductState(Long userId, ProductState productState);

    /**
     * @return только те состояния, которые есть у товаров в указанных категориях
     */
    @Query("select c from ProductCondition c where c.id in " +
            "(select distinct p.productCondition.id from Product p join p.productItems items " +
			"where p.category.id in ?1 " +
            /* учитывать только доступные товары - те товары,
           	 * которые находятся в состоянии PUBLISHED
           	 * и имеют неудаленные вещи в состоянии INITIAL)
             */
			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and items.deleteTime is null " +
			"and items.state = 'INITIAL') " +
            "order by c.sortOrder")
    List<ProductCondition> getActualConditions(List<Long> categories);

	@Query("select c from ProductCondition c where c.id in " +
			"(select distinct p.productCondition.id from Product p join p.productItems items " +
			"where p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and items.deleteTime is null " +
			"and items.state = 'INITIAL') " +
			"order by c.sortOrder")
	List<ProductCondition> getActualConditions();

	@Query("select distinct p.productCondition from Product p join p.productItems items " +
			"where p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and p.brand = :#{#brand} " +
			"and (p.category in " +
				"(select c from Category c " +
				"where c.leftOrder >= :#{#category?.leftOrder} and c.rightOrder <= :#{#category?.rightOrder} ) " +
				"or :#{#category} is null) " +
			"and items.deleteTime is null " +
			"and items.state = 'INITIAL' " +
			"order by p.productCondition.sortOrder")
	List<ProductCondition> getActualConditions(@Param("brand") Brand b, @Param("category") Category category);

    /**
     * @return только те бренды, товары которых есть в указанных категориях
     */
    @Query("select b from Brand b where b.id in " +
            "(select distinct p.brand.id from Product p join p.productItems items " +
			"where p.category.id in ?1 " +
			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and items.deleteTime is null " +
			"and items.state = 'INITIAL') " +
            "order by b.name")
    List<Brand> getActualBrands(List<Long> categories);

	@Query("select b from Brand b where b.id in " +
			"(select distinct p.brand.id from Product p join p.productItems items " +
			"where p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and items.deleteTime is null " +
			"and items.state = 'INITIAL') " +
			"order by b.name")
	List<Brand> getActualBrands();

    @Query("select distinct p.category from Product p join p.productItems items " +
                "where p.brand = ?1 " +
                "and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
                "and items.deleteTime is null " +
                "and items.state = 'INITIAL'")
    List<Category> getActualCategories(Brand b);

    List<Product> findTop20ByCategoryIn(List<Category> categories);

	/**
	 * @return число товаров с данным состоянием, время публикации которых позже указанного
	 */
	int countByProductStateAndPublishTimeAfter(ProductState s, LocalDateTime from);

	/**
	 * Ищет товары по полнотекстовому поиску
	 * Поиск осуществляется по имени текущей категории и именам всех родительских категорий
	 * @param query запрос вида Жен:* & изд:*
	 * @return список найденных товаров
	 */
	@Query(value =
			"SELECT " +
					"     p.* " +
					"    FROM ( " +
					"       SELECT " +
					"        c.id, " +
					"        string_agg(p.display_name || coalesce(' ' || p.singular_name, ''), ' ') AS fullText " +
					"       FROM category c " +
					"        JOIN category p ON p.left_order <= c.left_order AND p.right_order >= c.right_order " +
					"       WHERE p.parent_id IS NOT NULL " +
					"       GROUP BY c.id " +
					"      ) AS t " +
					"     JOIN category c ON c.id = t.id " +
					"     JOIN product p ON p.category_id = c.id " +
					"     JOIN brand b ON b.id = p.brand_id " +
					"              AND p.product_state = 'PUBLISHED' " +
					"     JOIN product_item pi ON pi.product_id = p.id " +
					"                 AND pi.state = 'INITIAL' " +
					"                 AND pi.delete_time IS NULL " +
					"     LEFT JOIN product_attribute_value_binding pavb ON pavb.product_id = p.id " +
					"     LEFT JOIN attribute_value av ON av.id = pavb.attribute_value_id " +
					"     WHERE (to_tsvector('russian', LOWER(fullText)) @@ to_tsquery('russian',LOWER(:query))) = TRUE " +
					"      OR (to_tsvector('english', LOWER(b.name)) @@ to_tsquery('english', LOWER(:query))) = TRUE " +
					"      OR (to_tsvector('russian', LOWER(av.value)) @@ to_tsquery('russian', LOWER(:query))) = TRUE " +
					"    GROUP BY p.id " +
					"    ORDER BY " +
					"    MAX(ts_rank_cd(to_tsvector('russian', LOWER(fullText)), to_tsquery('russian', LOWER(:query)))) DESC, " +
					"    MAX(ts_rank_cd(to_tsvector('english', LOWER(b.name)), to_tsquery('english', LOWER(:query)))) DESC, " +
					"    MAX(ts_rank_cd(to_tsvector('russian', LOWER(av.value)), to_tsquery('russian', LOWER(:query)))) DESC " +
					"    LIMIT :limit", // до тех пор, пока не появится пагинация
			nativeQuery = true
	)
	List<Product> fullTextSearch(@Param("query") String query, @Param("limit") int limit);

	/** У товара есть хотя бы одна вещь, доступная для продажи. */
	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +
			"where p = ?1 and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean hasAvailableItems(Product p);

	/** Если у всех доступных вещей данного товара одна цена, возвращает ее, иначе null */
	@Query(value = "select case when count(*) = 1 then min(current_price) else null end from " +
			"(select current_price from product_item " +
			"where product_id = ?1 and delete_time is null and state = 'INITIAL' " +
			"group by current_price) foo;"
			,nativeQuery =  true)
	Optional<BigDecimal> getItemsSinglePriceIfAny(Product p);

	/**
	 * @return true, если есть хотя бы одна винтажная доступная для покупки вещь в данных категориях
	 */
	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.vintage = true " +
			"and p.category.id in ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
    boolean getVintageProductsPresence(List<Long> categories);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.vintage = true " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getVintageProductsPresence();

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.vintage = true " +
			"and p.brand = ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getVintageProductsPresence(Brand b);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.vintage = true " +
			"and p.brand = :#{#brand} " +
			"and p.category in " +
                "(select c from Category c " +
				"where c.leftOrder >= :#{#category.leftOrder} and c.rightOrder <= :#{#category.rightOrder} " +
				"and c.rightOrder - c.leftOrder = 1) " +


			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getVintageProductsPresence(@Param("brand") Brand b, @Param("category") Category c);

	/**
	 * @return true, если есть хотя бы одна доступная для покупки вещь из новой коллекции
	 * в данных категориях
	 */
	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.isNewCollection = true " +
			"and p.category.id in ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getNewCollectionProductsPresence(List<Long> categories);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.isNewCollection = true " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getNewCollectionProductsPresence();

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.isNewCollection = true " +
			"and p.brand = ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getNewCollectionProductsPresence(Brand b);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.isNewCollection = true " +
			"and p.brand = :#{#brand} " +
			"and p.category in " +
                "(select c from Category c " +
                "where c.leftOrder >= :#{#category.leftOrder} and c.rightOrder <= :#{#category.rightOrder} " +
                "and c.rightOrder - c.leftOrder = 1) " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getNewCollectionProductsPresence(@Param("brand") Brand b, @Param("category") Category c);

	/**
	 * @return true, если есть хотя бы одна доступная для покупки вещь с меткой "наш выбор"
	 * в данных категориях
	 */
	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.ourChoiceStatusTime is not null " +
			"and p.category.id in ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getProductsWithOurChoicePresence(List<Long> categories);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.ourChoiceStatusTime is not null " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getProductsWithOurChoicePresence();

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.ourChoiceStatusTime is not null " +
			"and p.brand =?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getProductsWithOurChoicePresence(Brand b);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where p.ourChoiceStatusTime is not null " +
			"and p.brand = :#{#brand} " +
			"and p.category in " +
                "(select c from Category c " +
                "where c.leftOrder >= :#{#category.leftOrder} and c.rightOrder <= :#{#category.rightOrder} " +
                "and c.rightOrder - c.leftOrder = 1) " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getProductsWithOurChoicePresence(@Param("brand")Brand b, @Param("category")Category c);

	/**
	 * @return true, если есть хотя бы одна доступная для покупки вещь со сниженной ценой
	 * в данных категориях
	 */
	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where pi.currentPrice < pi.startPrice " +
			"and p.category.id in ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getSaleProductsPresence(List<Long> categories);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where pi.currentPrice < pi.startPrice " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getSaleProductsPresence();

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where pi.currentPrice < pi.startPrice " +
			"and p.brand = ?1 " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getSaleProductsPresence(Brand b);

	@Query("select case when(count(p) > 0) then true else false end " +
			"from Product p join p.productItems pi " +

			"where pi.currentPrice < pi.startPrice " +
			"and p.brand = :#{#brand} " +
			"and p.category in " +
                "(select c from Category c " +
                "where c.leftOrder >= :#{#category.leftOrder} and c.rightOrder <= :#{#category.rightOrder} " +
                "and c.rightOrder - c.leftOrder = 1) " +

			"and p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL'")
	boolean getSaleProductsPresence(@Param("brand")Brand b, @Param("category")Category c);

	@Query("select count(p) from Product p join p.productItems pi where p.productState = ?1 and p.seller = ?3 " +
			"and pi.state = ?2 and pi.deleteTime is null")
	int countSellerProducts(ProductState productState, ProductItem.State s, User seller);

	/** нет времени для нормального решения {@link su.reddot.infrastructure.export.vk.DefaultExporter}*/
	@Query("select distinct p from Product p join p.productItems pi " +
			"where p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			" and pi.deleteTime is null and pi.state = 'INITIAL'")
	List<Product> getAvailableProducts();

	Integer countProductBySellerAndProductState(User user, ProductState published);

	@Query("select distinct p from Product p join p.productItems pi " +
			"where p.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.deleteTime is null and pi.state = 'INITIAL' " +
			"and p.seller = :#{#seller} ")
	List<Product> getAvailableProductsBySeller(@Param("seller")User seller);
}