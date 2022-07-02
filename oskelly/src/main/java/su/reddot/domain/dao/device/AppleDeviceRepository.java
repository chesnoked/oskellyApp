package su.reddot.domain.dao.device;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.device.AppleDevice;
import su.reddot.domain.model.user.User;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
public interface AppleDeviceRepository extends JpaRepository<AppleDevice, Long> {

	List<AppleDevice> findByUser(User u);
}
