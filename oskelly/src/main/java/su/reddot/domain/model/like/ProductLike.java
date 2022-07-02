package su.reddot.domain.model.like;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.product.Product;

import javax.persistence.*;

/**
 * @author Vitaliy Khludeev on 13.09.17.
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
public class ProductLike extends Like<Product> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Product product;

	@Override
	public void setLikable(Product likable) {
		product = likable;
	}

	@Override
	public Product getLikable() {
		return product;
	}
}
