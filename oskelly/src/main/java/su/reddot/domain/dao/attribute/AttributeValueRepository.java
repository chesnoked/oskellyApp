package su.reddot.domain.dao.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.attribute.SimpleAttributeValue;

import java.util.List;
import java.util.Optional;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    /**
     * Получить все значения данного атрибута, упорядоченные по возрастанию идентификатора
     * @param attribute атрибут, для которого нужно получить его значения
     * @return список значений атрибута
     */
    List<SimpleAttributeValue> findAllValuesByAttributeOrderById(Attribute attribute);
    Optional<AttributeValue> findFirstByAttributeAndTransliterateValue(Attribute attribute, String value);
    List<AttributeValue> findAllByTransliterateValue(String value);
}
