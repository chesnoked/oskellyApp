package su.reddot.domain.model.product;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Описывает состояние товара (как новый, так себе и т.п.)
 */
@Entity
@Data
public class ProductCondition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private Long sortOrder;
}
