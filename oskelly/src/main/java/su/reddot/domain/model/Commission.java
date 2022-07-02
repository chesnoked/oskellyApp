package su.reddot.domain.model;

import lombok.Getter;
import lombok.Setter;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Vitaliy Khludeev on 06.08.17.
 */
@Entity
@Getter
@Setter
public class Commission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal value;

	@Enumerated(EnumType.STRING)
	private Type type;

	@ManyToOne
	private Category category;

	@ManyToOne
	private User user;

	private BigDecimal startPrice;

	private BigDecimal endPrice;

	public enum Type {
		STANDARD,
		PRO_STANDARD,
		CATEGORY,
		USER,
		TURBO,
		NEW_COLLECTION
	}
}
