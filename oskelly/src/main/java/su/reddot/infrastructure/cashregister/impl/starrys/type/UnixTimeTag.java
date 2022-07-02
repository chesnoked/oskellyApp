package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
class UnixTimeTag extends Tag {
    @JsonProperty("Value")
    private LocalDateTime value;
}
