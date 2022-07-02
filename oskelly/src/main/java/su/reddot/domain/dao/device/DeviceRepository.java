package su.reddot.domain.dao.device;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.device.Device;

import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 26.01.18.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

	Optional<Device> findFirstByToken(String token);
}
