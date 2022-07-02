package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @JsonNaming(CustomNamingStrategy.class)
class SingleCommandResponse {

    private String path;
    private Response response;

}
