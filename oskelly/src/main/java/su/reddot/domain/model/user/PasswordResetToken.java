package su.reddot.domain.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.time.ZonedDateTime;

@Embeddable
@NoArgsConstructor /* по требованию JPA */
@Getter @Setter @Accessors(chain = true)
public class PasswordResetToken {

    private String value;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime usedAt;

    public PasswordResetToken(String value) {
        this.value = value;
        createdAt = ZonedDateTime.now();
    }

    public boolean isExpired() {
        return createdAt.plusHours(2).isBefore(ZonedDateTime.now())
               || usedAt != null;
    }
}
