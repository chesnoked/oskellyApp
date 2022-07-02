package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "TagType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ByteTag.class, name = "byte"),
        @JsonSubTypes.Type(value = ByteArrayTag.class, name = "byte[]"),
        @JsonSubTypes.Type(value = UnixTimeTag.class, name = "unixtime"),
        @JsonSubTypes.Type(value = Uint32Tag.class, name = "uint32"),
        @JsonSubTypes.Type(value = MoneyTag.class, name = "money"),
        @JsonSubTypes.Type(value = NodeTag.class, name = "stlv"),
        @JsonSubTypes.Type(value = QuantityTag.class, name = "qty"),
        @JsonSubTypes.Type(value = RateTag.class, name = "rate"),
        @JsonSubTypes.Type(value = Uint16Tag.class, name = "uint16"),
        @JsonSubTypes.Type(value = StringTag.class, name = "string"),
})
@Getter
@Setter
abstract class Tag {
    @JsonProperty("TagID")
    private long id;
}
