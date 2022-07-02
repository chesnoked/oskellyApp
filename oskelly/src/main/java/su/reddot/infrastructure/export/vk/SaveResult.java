package su.reddot.infrastructure.export.vk;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
class SaveResult {

    private List<SavedImage> response;

    @Getter @Setter @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class SavedImage {
        private String id;
    }
}
