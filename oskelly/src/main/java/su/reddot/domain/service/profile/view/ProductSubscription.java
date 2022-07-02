package su.reddot.domain.service.profile.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/** Данные о подписке на появление товаров с заданными атрибутами*/
@Getter @Setter @Accessors(chain = true)
public class ProductSubscription {

    private long id;

    private String createdAt;

    private String brand;

    private List<String> categories;

    private List<String> attributes;

    private String condition;

    private Size size;

    @Getter @Setter @Accessors(chain = true)
    public static class Size {
        private String type;
        private String value;
    }
}


