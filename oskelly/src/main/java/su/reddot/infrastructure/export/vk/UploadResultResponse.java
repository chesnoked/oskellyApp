package su.reddot.infrastructure.export.vk;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * Структура, которую присылает ВК в качестве результата выполнения
 * процедуры загрузки файла изображения.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter
class UploadResultResponse {

    private String photo;

    private String server;

    private String hash;

    private String cropData;

    private String cropHash;
}
