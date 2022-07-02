package su.reddot.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 29.08.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String text;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime publishTime;

	@ManyToOne
	private User publisher;

	@ManyToOne
	private Product product;
}
