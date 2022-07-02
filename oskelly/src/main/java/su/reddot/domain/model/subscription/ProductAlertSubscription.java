package su.reddot.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class ProductAlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private User subscriber;

    @ManyToOne
    private Brand brand;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Size size;
    /**
     * Отображаемый тип Размера
     */
    @Enumerated(EnumType.STRING)
    private SizeType viewSizeType;

    @ManyToOne
    private ProductCondition productCondition;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "productAlertSubscription", cascade = CascadeType.ALL)
    private List<ProductAlertAttributeValueBinding> attributeValueBindings = new ArrayList<>();

    public void setAttributeValueBinding(ProductAlertAttributeValueBinding binding) {
        binding.setProductAlertSubscription(this);
        attributeValueBindings.add(binding);
    }
}
