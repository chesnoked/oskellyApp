package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class Uint32Tag extends Tag {

    /** Апи передает unsigned значения,
     * которые в int не влезают (int хранит значения со знаком) */
    @JsonProperty("Value")
    private long value;
}
