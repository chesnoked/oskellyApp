package su.reddot.domain.model.device;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 26.01.18.
 */
@Entity
@Inheritance
@Getter
@Setter
@Accessors(chain = true)
public abstract class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;

	@NotNull
	private String token;
}
