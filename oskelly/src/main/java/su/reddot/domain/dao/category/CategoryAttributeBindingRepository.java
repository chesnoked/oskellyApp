package su.reddot.domain.dao.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.category.CategoryAttributeBinding;

import java.util.List;

public interface CategoryAttributeBindingRepository extends JpaRepository<CategoryAttributeBinding, Long> {
	/**
	 * Получить список всех атрибутов для данных категорий
	 *
	 * @param categories список категорий
	 * @return список атрибутов категорий
	 */
	List<CategoryAttributeBinding> findAllAttributesByCategoryInOrderByAttribute(List<Category> categories);

	@Query("select c.category from CategoryAttributeBinding c where c.attribute=:attribute")
	Category findByAttribute(@Param("attribute") Attribute attribute);
}
