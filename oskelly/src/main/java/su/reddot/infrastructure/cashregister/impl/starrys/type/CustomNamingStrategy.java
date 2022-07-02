package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Имена полей ответов апи всегда начинаются с заглавной буквы.
 * Стратегия преобразовывает имена в стандартный snake case формат.
 **/
class CustomNamingStrategy extends PropertyNamingStrategy
{
    @Override
    public String nameForField(MapperConfig config,
                               AnnotatedField field, String defaultName) {
        return convert(defaultName);

    }
    @Override
    public String nameForGetterMethod(MapperConfig config,
                                      AnnotatedMethod method, String defaultName) {
        return convert(defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig config,
                                      AnnotatedMethod method, String defaultName) {
        return convert(defaultName);
    }

    private String convert(String defaultName ) {

        if (defaultName.length() == 0) return defaultName;

        return defaultName.substring(0, 1).toUpperCase()
                + defaultName.substring(1);
    }

}
