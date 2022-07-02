package su.reddot.domain.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.product.ItemsSummaryBySize;
import su.reddot.domain.service.product.ProductSizeMapping;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long>, QueryDslPredicateExecutor<ProductItem> {

	List<ProductItem> findByProductAndDeleteTimeIsNull(Product product);

	Optional<ProductItem> findFirstByProductAndDeleteTimeIsNullOrderById(Product product);

	Optional<ProductItem> findFirstByProductOrderById(Product product);

	/**
	 * TODO: передавать сюда id товара и размер и выбирать любую незарезервированную позицию
	 * Получаем незарезервированную никем позицию товара и устанавливаем на него блокировку (select for update)
	 * @param id идентификатор позиции товара
	 * @return незарезервированная позиция товара
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<ProductItem> findByIdAndReserveExpireTimeIsNullAndDeleteTimeIsNull(Long id);

	List<ProductItem> findBySizeIdInAndProductAndDeleteTimeIsNullOrderBySizeId(List<Long> sizes, Product p);

	Optional<ProductItem> findByIdAndProductSeller(Long id, User seller);

	int countAllByProductSellerAndState(User seller, ProductItem.State s);

	/**
	 * @param p обобщенный товар
	 * @return конкретный товар с наименьшей ценой
	 */
	ProductItem findFirstByProductAndDeleteTimeIsNullOrderByCurrentPriceAsc(Product p);

	@Query("select new su.reddot.domain.service.product.ProductSizeMapping(s, count(pi), pi.currentPriceWithoutCommission, min(pi.currentPrice)) " +
			"from ProductItem pi " +
			"left join pi.size s " +
			"where pi.product = ?1 and pi.deleteTime is null and (pi.state = ?2 or ?2 is null)" +
			"group by s, pi.currentPriceWithoutCommission")
	Set<ProductSizeMapping> getProductSizeMappings(Product p, ProductItem.State s);


	/**
	 * @param p товар
	 * @param s учитывать только вещи с данным статусом. Если не задан, учитывать вещи с любым статусом.
	 *
	 * @return число доступных вещей по каждому размеру, цена - минимальная в пределах одного размера.
	 *             Например, 42 вещи размера 36 с минимальной ценой 911,
	 *          			 56 вещей размера Y с минимальной ценой 420.
	 *             Данные упорядочены по размеру в порядке возрастания размера.
	 *             На данный момент размер больше другого размера, если у него больше идентификатор.
	 */
	@Query("select new su.reddot.domain.service.product.ItemsSummaryBySize(" +
			"pi.size, count(pi), case when count(distinct current_price) > 1 then true else false end, min(pi.currentPrice), min(pi.startPrice)) " +
			"from ProductItem pi " +
			"where pi.product.id = ?1 " +
			"and (pi.state = ?2 or ?2 is null) " +
			"and (pi.size.id in ?3 or ?3 is null) " +
			"and pi.deleteTime is null " +
			"and pi.product.sizeType is not null " +
			"group by pi.size order by pi.size")
	List<ItemsSummaryBySize> getAvailableItemsSummaryGroupedBySize(Long p, ProductItem.State s, List<Long> interestingSizes);


	/**
	 * @param productId товар, среди вещей которого нужно найти подходящую для покупки вещь...
	 * @param sizeId ... по размеру
	 * @param price ... и цене
	 * @return первая вещь, которая доступна для покупки, принадлежит заданному товару, с заданной ценой и размером
	 * @see ProductItemRepository#getFirstAvailable(Long, Long)
	 */
	@Deprecated
	@Query(
			value = "SELECT * FROM product_item JOIN product ON product_item.product_id = product.id " +
			"WHERE product_id = ?1 AND size_id = ?2 AND current_price = ?3 " +
			"AND state = 'INITIAL' " +
			"AND product_state = 'PUBLISHED' " +
			"AND delete_time IS NULL " +
			"LIMIT 1",
			nativeQuery = true
	)
	Optional<ProductItem> getFirstAvailable(Long productId, Long sizeId, BigDecimal price);

	/**
	 * @param productId товар, среди вещей которого нужно найти подходящую для покупки вещь...
	 * @param sizeId ... по размеру
	 * @return первая вещь, которая доступна для покупки, принадлежит заданному товару, с заданной ценой и размером
	 */
	@Query(
			value = "SELECT * FROM product_item JOIN product ON product_item.product_id = product.id " +
					"WHERE product_id = ?1 AND size_id = ?2 " +
					"AND state = 'INITIAL' " +
					"AND product_state = 'PUBLISHED' " +
					"AND delete_time IS NULL " +
					"ORDER BY current_price " +
					"LIMIT 1",
			nativeQuery = true
	)
	Optional<ProductItem> getFirstAvailable(Long productId, Long sizeId);

	Long countByProductAndSizeAndCurrentPriceWithoutCommissionAndDeleteTimeIsNull(Product p, Size s, BigDecimal price);

	@Modifying
	@Query(
			value = "UPDATE product_item SET delete_time = :deleteTime WHERE id IN (" +
					" SELECT pi.id FROM product_item pi WHERE pi.product_id = :productId AND pi.size_id = :sizeId AND pi.state = 'INITIAL' AND pi.current_price_without_commission = :price AND pi.delete_time IS NULL LIMIT :limit" +
					")",
			nativeQuery = true)
	void setDeleteTimeByProductAndSizeAndPrice(
			@Param("deleteTime") Date deleteTime,
			@Param("productId") Long productId,
			@Param("sizeId") Long sizeId,
			@Param("price") BigDecimal price,
			@Param("limit") Integer limit
		);


	@Modifying
	@Query(
			value = "UPDATE product_item SET delete_time = :deleteTime WHERE id IN (" +
					" SELECT pi.id FROM product_item pi WHERE pi.product_id = :productId AND pi.size_id = :sizeId AND pi.state = 'INITIAL' AND pi.current_price_without_commission IS NULL AND pi.delete_time IS NULL LIMIT :limit" +
					")",
			nativeQuery = true)
	void setDeleteTimeByProductAndSizeAndPriceIsNull(
			@Param("deleteTime") Date deleteTime,
			@Param("productId") Long productId,
			@Param("sizeId") Long sizeId,
			@Param("limit") Integer limit
	);

	@Modifying
	@Query(
			value = "UPDATE product_item SET delete_time = :deleteTime WHERE id IN (" +
					" SELECT pi.id FROM product_item pi WHERE pi.product_id = :productId AND pi.size_id IS NULL AND pi.state = 'INITIAL' AND pi.current_price_without_commission = :price AND pi.delete_time IS NULL LIMIT :limit" +
					")",
			nativeQuery = true)
	void setDeleteTimeByProductAndSizeIsNullAndPrice(
			@Param("deleteTime") Date deleteTime,
			@Param("productId") Long productId,
			@Param("price") BigDecimal price,
			@Param("limit") Integer limit
	);

	@Modifying
	@Query(
			value = "UPDATE product_item SET delete_time = :deleteTime WHERE id IN (" +
					" SELECT pi.id FROM product_item pi WHERE pi.product_id = :productId AND pi.size_id IS NULL AND pi.state = 'INITIAL' AND pi.current_price_without_commission IS NULL AND pi.delete_time IS NULL LIMIT :limit" +
					")",
			nativeQuery = true)
	void setDeleteTimeByProductAndSizeIsNullAndPriceIsNull(
			@Param("deleteTime") Date deleteTime,
			@Param("productId") Long productId,
			@Param("limit") Integer limit
	);

	/**
     * native query используется только из-за необходимости ограничивать выборку через limit
	 * @return вещь, которую можно купить, с такими же размером и ценой, что и у переданной вещи (кроме этой вещи)
	 */
	@Query(value = "SELECT * FROM product_item JOIN product on product_item.product_id = product.id " +
			"WHERE current_price = :#{#item.currentPrice} " +
			"AND size_id = :#{#item.size.id} " +
			"AND state = 'INITIAL' " +
			"AND product_id = :#{#item.product} " +
			"AND product_state = 'PUBLISHED' " +
			"AND delete_time IS NULL " +
			"AND product_item.id <> :#{#item.id} " +
			"LIMIT 1",
			nativeQuery = true
	)
	Optional<ProductItem> getItemLikeThisThatCanBeOrdered(@Param("item") ProductItem item);

	/**
	 * @return вещь, которую можно купить, с такими же размером и ценой, что и у переданной вещи
	 */
	@Query(value = "SELECT * FROM product_item JOIN product on product_item.product_id = product.id " +
			"WHERE current_price = :#{#item.currentPrice} " +
			"AND size_id = :#{#item.size.id} " +
			"AND state = 'INITIAL' " +
			"AND product_id = :#{#item.product} " +
			"AND product_state = 'PUBLISHED' " +
			"AND current_price = :#{#price} " +
			"AND delete_time IS NULL " +
			"LIMIT 1 FOR UPDATE",
			nativeQuery = true
	)
	Optional<ProductItem> getItemForSale(@Param("item") ProductItem item, @Param("price") BigDecimal interestingPrice);

	@Query("select item from ProductItem item " +
			"where item.id = ?1 " +
			"and item.product.seller = ?2 " +
			"and item.state in ?3 " +
			"and item.deleteTime is null " +
			"and item.product.productState = 'PUBLISHED'")
    Optional<ProductItem> getAvailableItemBySellerAndState(Long id, User user, List<ProductItem.State> states);

	@Query("select distinct pi.size.id from ProductItem pi where pi.product.category in " +
				"(select c from Category c where c.leftOrder >= :#{#cat.leftOrder} and c.rightOrder <= :#{#cat.rightOrder} and c.rightOrder = c.leftOrder + 1) " +
			"and pi.size.id in " +
			"	(select s from Size s left join s.category c where c.leftOrder = " +
			"		(select max(c.leftOrder) from Size s left join s.category c where c in " +
			"			(select c from Category c where c.leftOrder <= :#{#cat.leftOrder} and c.rightOrder >= :#{#cat.rightOrder}))) " +

			"and (pi.product.brand = :#{#brand} or :#{#brand} is null) " +

			"and pi.product.productState = su.reddot.domain.model.product.ProductState.PUBLISHED " +
			"and pi.state = 'INITIAL' " +
			"and pi.deleteTime is null " +
			"order by pi.size.id")
	List<Long> getActualSizes(@Param("brand") Brand b, @Param("cat") Category c);

	default List<Long> getActualSizes(@Param("cat") Category c) {
		return getActualSizes(null , c);
	}

	List<ProductItem> getAllByStateAndDeleteTimeIsNull(ProductItem.State state);

	List<ProductItem> getAllByStateAndProductAndDeleteTimeIsNull(ProductItem.State state, Product product);

	List<ProductItem> getAllByStateInAndDeleteTimeIsNull(List<ProductItem.State> states);

	List<ProductItem> getAllByProduct(Product product);
}
