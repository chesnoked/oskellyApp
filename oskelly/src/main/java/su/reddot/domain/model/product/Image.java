package su.reddot.domain.model.product;

import lombok.Data;
import su.reddot.infrastructure.service.imageProcessing.ImageProcessor;

import javax.persistence.*;
import java.io.File;

@Entity
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @ManyToOne
    private Product product;

    private Integer photoOrder;

    private boolean isMain;

    String getImagePath(ImageProcessor.ProcessingType processingType) {
        File file = new File(imagePath);
        String fileName = processingType.getPrefix() + "-" + file.getName();
        return new File(file.getParentFile(), fileName).getPath();
    }
}
