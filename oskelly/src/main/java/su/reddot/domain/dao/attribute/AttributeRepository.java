package su.reddot.domain.dao.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.attribute.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    /**
     * Временный метод
     */
    Attribute findByName(String name);
}
