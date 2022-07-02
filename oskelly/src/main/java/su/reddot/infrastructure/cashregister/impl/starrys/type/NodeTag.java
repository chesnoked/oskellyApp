package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
class NodeTag extends Tag {
    @JsonProperty("Value")
    private List<Tag> value;
}
