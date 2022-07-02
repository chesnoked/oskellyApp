package su.reddot.infrastructure.export.vk;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class UploadUrlResponse {

    private Response response;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Getter @Setter
    static class Response {
        private String uploadUrl;
    }
}
