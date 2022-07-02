package su.reddot.domain.dao;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {
	@Override
	public Timestamp convertToDatabaseColumn(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		long millis = dateTime.toInstant().getEpochSecond() * 1000L;
		return new Timestamp(millis);
	}

	@Override
	public ZonedDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		if (sqlTimestamp == null) {
			return null;
		}
		Instant instant = sqlTimestamp.toInstant();
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
}
