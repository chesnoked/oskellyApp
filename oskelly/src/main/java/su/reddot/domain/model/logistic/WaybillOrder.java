package su.reddot.domain.model.logistic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Vitaliy Khludeev on 31.08.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class WaybillOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * uuid будем передавать в систему логистов
	 */
	private UUID uuid;

	/**
	 * Идентификатор в системе логистов
	 */
	private String externalSystemId;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;
}
