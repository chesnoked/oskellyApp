package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class ByteArrayTag extends Tag {

    /** Из документации: массив байтов <b>в кодировке Base64</b> */
    @JsonProperty("Value")
    private String value;
}
