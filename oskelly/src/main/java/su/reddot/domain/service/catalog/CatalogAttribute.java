package su.reddot.domain.service.catalog;

import lombok.Value;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.SimpleAttributeValue;

import java.util.List;

/**
 * Атрибут и его возможные значения (справочник)
 */
@Value
public class CatalogAttribute {
    private final Attribute attribute;
    private final List<SimpleAttributeValue> values;
}
