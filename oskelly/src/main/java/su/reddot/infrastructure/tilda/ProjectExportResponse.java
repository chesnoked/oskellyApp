package su.reddot.infrastructure.tilda;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
class ProjectExportResponse {

    private String status;
    private Result result;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Getter @Setter
    static class Result {
        private Long id;
        private String title;
        private List<ExportFileMap> css;
        private List<ExportFileMap> js;
        private List<ExportFileMap> images;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @Getter @Setter
        static class ExportFileMap {
            private String from;
            private String to;

        }
    }
}
