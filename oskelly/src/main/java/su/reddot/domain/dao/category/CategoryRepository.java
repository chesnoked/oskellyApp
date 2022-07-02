package su.reddot.domain.dao.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.category.Category;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, QueryDslPredicateExecutor<Category> {

    /**
     * Получить список всех категорий, за исключением корневой ( служебной ) категории
     * @return список всех категорий в порядке от левой дочерней категории, к правой, рекурсивно.
     * Например, дерево категорий:
     *
     *       Root
     *    /   |   \
     *   A    B    C
     * / |    |    | \
     * D E    G    H  I
     *
     * будет возвращено в виде списка категорий:
     *
     * A D E B G C H I
     */
    @Query("select c from Category c where c.leftOrder > 1 order by c.leftOrder")
    List<Category> findAll();

    /**
     * Получить список всех предков для данной категории, от дальних к близким,
     * <b>за исключением корневой (служебной) категории</b>.
     * @param categoryId идентификатор категории, для которой нужно получить ее предков.
     * @return список категорий - предков
     */
    @Query("select c from Category c " +
            "where c.leftOrder < (select c.leftOrder from Category c where c.id = ?1) " +
            "and c.rightOrder > (select c.rightOrder from Category c where c.id = ?1)" +
            "and c.leftOrder <> 1" +
            "order by c.leftOrder"
    )
    List<Category> findAllParents(Long categoryId);


    /**
     * Получить список всех непосредственных категорий-детей для данной категории-родителя.
     * @param parentId идентификатор родительской категории
     * @return список дочерних категорий
     */
    @Query("select c from Category c where c.parent.id = ?1 order by c.leftOrder")
    List<Category> findChildrenCategories(Long parentId);

    /**
     * Получить список дочерних категорий - "листьев", то есть тех категорий,
     * у которых нет собственных дочерних категорий.
     *
     * Например, если существует дерево категорий:
     *
     *       Root
     *    /   |   \
     *   A    B    C
     * / |    |    | \
     * D E    G    H  I
     *      /   \
     *     J     K
     *
     * то для категории B метод вернет категории J, K.
     * @param parentId идентификатор родительской категории
     * @return список категорий - "листьев" для данной родительской категории
     */
    @Query("select c from Category c " +
            "where c.leftOrder > (select c.leftOrder from Category c where c.id = ?1) " +
            "and c.rightOrder < (select c.rightOrder from Category c where c.id = ?1)" +
            "and c.rightOrder = c.leftOrder + 1" +
            "order by c.leftOrder"
    )
    List<Category> findLeafCategories(Long parentId);

    /**
     * Получить список всех категорий - листьев дерева.
     * @return список всех категорий - листьев.
     */
    @Query("select c from Category c where c.rightOrder = c.leftOrder + 1 order by c.leftOrder")
    List<Category> findAllLeafCategories();

    /**
     * Получить список корневых категорий.
     * @return список корневых категорий.
     */
    @Query("select c from Category c where c.parent = " +
            "(select c.id from Category c where c.leftOrder = 1)")
    List<Category> findRootCategories();

    /**
     * @return список идентификаторов категорий, в которых нет ни одного товара.
     */ @Query(value =  "SELECT p_id FROM (SELECT p_id, coalesce(sum(amount), 0) amount" +
            "  FROM (" +
            "         SELECT" +
            "           p.id  p_id," +
            "           c.id  c_id" +
            "         FROM category p CROSS JOIN category c" +
            "         WHERE" +
            "           p.left_order <= c.left_order AND p.right_order >= c.right_order AND" +
            "           c.right_order - c.left_order = 1 AND" +
            "           p.left_order <> 1) cats" +
            "    LEFT JOIN (" +
            "                SELECT" +
            "                  category_id," +
            "                  count(*) amount" +
            "                FROM product INNER JOIN product_item ON product_state = 'PUBLISHED' and product.id = product_item.product_id AND delete_time IS NULL AND state = 'INITIAL'" +
            "                GROUP BY category_id) product_by_category" +
            "                ON cats.c_id = product_by_category.category_id" +
            "  GROUP BY p_id) t" +
            "  WHERE amount = 0",

            nativeQuery = true)
    List<BigInteger> findEmpty();

    /**
     * Изменить левый порядок всех категорий, которые будут расположены <b>правее</b>
     * в дереве относительно вставляемой категории.
     * При добавлении новой категории для сохранения достоверности данных
     * нужно также <b>обязательно</b> вызвать
     * {@link #shiftRightOrdersBeforeInsertingNewCategory}
     *
     *          Root Element
     *        /  |  \      \
     *       A  X   D*     G*
     *     / |  ^   \ \     \
     *    B C   ^   E* F*   H*
     *          ^
     *          +-- вставляемая категория
     *
     * (*) - категории, у которых изменятся левые порядки
     *
     * @param newCategoryLeftOrder левый порядок новой категории
     */
    @Modifying(clearAutomatically = true)
    @Query("update Category c set c.leftOrder = c.leftOrder + 2 where c.leftOrder >= ?1")
    void shiftLeftOrdersBeforeInsertingNewCategory(int newCategoryLeftOrder);

    /**
     * Изменить правый порядок всех категорий, которые расположены <b>правее</b>
     * в дереве относительно вставляемой категории, а также порядок всех ее предков.
     * При добавлении новой категории для сохранения достоверности данных
     * нужно также <b>обязательно</b> вызвать
     * {@link #shiftLeftOrdersBeforeInsertingNewCategory}
     *
     * @param newCategoryLeftOrder левый порядок новой категории
     */
    @Modifying(clearAutomatically = true)
    @Query("update Category c set c.rightOrder = c.rightOrder + 2 where c.rightOrder >= ?1")
    void shiftRightOrdersBeforeInsertingNewCategory(int newCategoryLeftOrder);

    @Query("select c from Category c " +
            "where c.leftOrder = " +
                "(select min(c.leftOrder) from Category c " +
                "where c.leftOrder < :#{#cat.leftOrder} " +
                "and c.rightOrder > :#{#cat.rightOrder} and c.leftOrder <> 1 " +
                "and (c.parent = :#{#parent} or :#{#parent} is null))")
    Optional<Category> findNearest(@Param("cat") Category c, @Param("parent") Category nullableParent);
}
