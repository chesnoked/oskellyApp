package su.reddot.domain.dao.product.custom;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import su.reddot.domain.model.product.Product;

public interface ProductRepositoryCustom {

	/**
     * Найти товары по заданным критериям.
	 * @param predicate критерии, по которым выполняется фильтрация товаров
	 * @param page интересующие страница и сортировка
	 * @return товары, которые удовлетворяют указанным критериям поиска,
	 */
	Page<Product> find(Predicate predicate, Pageable page);

}
