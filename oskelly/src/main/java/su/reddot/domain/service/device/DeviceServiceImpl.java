package su.reddot.domain.service.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.device.DeviceRepository;
import su.reddot.domain.model.device.AndroidDevice;
import su.reddot.domain.model.device.AppleDevice;
import su.reddot.domain.model.device.Device;
import su.reddot.domain.model.user.User;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * @author Vitaliy Khludeev on 26.01.18.
 */
@Component
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

	private final DeviceRepository deviceRepository;

	@Override
	public void saveAppleDevice(String token, User user) {
		saveDevice(token, user, AppleDevice::new);
	}

	@Override
	public void saveAndroidDevice(String token, User user) {
		saveDevice(token, user, AndroidDevice::new);
	}

	private void saveDevice(String token, User user, Supplier<Device> newDevice) {
		Device device = deviceRepository.findFirstByToken(token).orElseGet(newDevice);
		device.setUser(user);
		device.setToken(token);
		if(device.getCreateTime() == null) {
			device.setCreateTime(ZonedDateTime.now());
		}
		deviceRepository.save(device);
	}
}
