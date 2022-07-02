package su.reddot.domain.model.attribute;

/**
 * Представление {@link AttributeValue}, которое не содержит в себе
 * ссылки на родительский атрибут
 */
public interface SimpleAttributeValue {
    Long getId();
    String getValue();
}
