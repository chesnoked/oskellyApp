package su.reddot.domain.service.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.service.catalog.size.CatalogSize;

import java.util.List;

/** Шаблон создания подписки на получение уведомлений
 * о появлении в магазине подходящих товаров. */
@Getter @Setter @Accessors(chain = true)
public class AlertTemplate {

    private long         categoryId;
    private String       categoryName;
    private List<String> parentCategories;

    private List<Brand> brands;

    private List<Attribute> attributes;

    private List<Condition> conditions;

    /** @apiNote может быть null, если категория товара не имеет такого понятия как размер. */
    private List<CatalogSize> sizes;

    /** Путь выбранного товара в дереве категорий.
     * Раздел в путь не входит.
     * Если категория товара находится на 3 уровне, она входит в путь, иначе - нет.
     * */
    public String getPath() {
        String allExceptRoot = String.join(" › ", parentCategories.subList(1, parentCategories.size()));

        return parentCategories.size() > 2?
                allExceptRoot
                : allExceptRoot + " › " + categoryName;
    }

    @Getter @Setter @Accessors(chain = true)
    public static class Brand {
        private long   id;
        private String name;
    }

    @Getter @Setter
    public static class Attribute {
        private long id;
        private String name;
        private List<Value> values;

        @Getter @Setter
        public static class Value {
            private long id;
            private String name;
        }
    }

    @Getter @Setter
    public static class Condition {
        private long id;
        private String name;
    }
}