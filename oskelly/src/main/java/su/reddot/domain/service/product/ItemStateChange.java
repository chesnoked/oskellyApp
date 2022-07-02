package su.reddot.domain.service.product;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.ProductItem;

import java.time.ZonedDateTime;

@Getter @Setter @Accessors(chain = true)
public class ItemStateChange {

    private ZonedDateTime at;

    private ProductItem.State state;
}
