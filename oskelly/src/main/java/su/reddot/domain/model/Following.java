package su.reddot.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 22.09.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Following {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@NotNull
	private User follower;

	@ManyToOne
	@NotNull
	private User following;

	@Convert(converter = ZonedDateTimeConverter.class)
	@NotNull
	private ZonedDateTime createTime = ZonedDateTime.now();
}
