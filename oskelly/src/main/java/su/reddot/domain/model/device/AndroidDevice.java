package su.reddot.domain.model.device;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;

/**
 * @author Vitaliy Khludeev on 05.02.18.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class AndroidDevice extends Device {
}
