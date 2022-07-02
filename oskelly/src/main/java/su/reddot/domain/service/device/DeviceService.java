package su.reddot.domain.service.device;

import su.reddot.domain.model.user.User;

/**
 * @author Vitaliy Khludeev on 26.01.18.
 */
public interface DeviceService {

	void saveAppleDevice(String token, User user);

	void saveAndroidDevice(String token, User user);
}
