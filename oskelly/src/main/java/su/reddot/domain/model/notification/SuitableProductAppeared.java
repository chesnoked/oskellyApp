package su.reddot.domain.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Optional;

@Entity
@Getter @Setter @Accessors(chain = true)
public class SuitableProductAppeared extends Notification {

    @ManyToOne
    private Product product;

    @Override
    public Optional<User> getInitiator() { return Optional.empty(); }

    @Override
    public Optional<User> getTargetUser() { return Optional.empty(); }

    @Override
    public Optional<String> getImageOfTargetObject() {
        return Optional.ofNullable(product.getImagePreview());
    }

    @Override
    public String getEmptyImageOfTargetObject() { return null; }

    @Override
    public String getUrlOfTargetObject() {
        return String.format("/products/%s", product.getId());
    }

    @Override
    public String getBaseMessage() {

        return String.format("Опубликован товар, который вы искали - %s %s",
                product.getBrand().getName(),
                product.getDisplayName());
    }

    @Override
    public NotificationType getType() {
        return NotificationType.NOTIFICATION;
    }
}
