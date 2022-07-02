package su.reddot.domain.dao;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    public static Timestamp convertToTimestamp(LocalDateTime localDateTime){
        return (localDateTime == null ? null : Timestamp.valueOf(localDateTime));
    }

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
        return convertToTimestamp(locDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime());
    }
}
