package su.reddot.domain.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author sergeykultishev  on 27.09.17
 */
@Entity
@Accessors(chain = true)
@Getter @Setter
public class PublicationPhotoSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @ManyToOne
    @JsonIgnore
    private Category category;

    private String imagePath;

    private Integer photoOrder;
}
