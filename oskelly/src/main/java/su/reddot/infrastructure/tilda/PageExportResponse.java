package su.reddot.infrastructure.tilda;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
class PageExportResponse {
    private String status;
    private Result result;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Getter @Setter
    static class Result {
        private Long id;
        private Long projectid;
        private String alias;
        private String title;
        private String html;
        private List<ExportFileMap> images;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @Getter @Setter
        static class ExportFileMap {
            private String from;
            private String to;

        }
    }

}
