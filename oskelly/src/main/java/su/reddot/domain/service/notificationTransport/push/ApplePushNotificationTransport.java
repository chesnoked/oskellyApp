package su.reddot.domain.service.notificationTransport.push;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.device.AppleDeviceRepository;
import su.reddot.domain.model.device.AppleDevice;
import su.reddot.domain.service.notificationTransport.NotificationTransport;
import su.reddot.domain.service.notificationTransport.NotificationTransportEvent;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
@Component
@RequiredArgsConstructor
public class ApplePushNotificationTransport implements NotificationTransport {

	private final AppleDeviceRepository deviceRepository;
	private final ApnsService service;

	@Override
	public void send(NotificationTransportEvent e) {
		String payload = APNS.newPayload().alertBody(e.getNotification().getFullMessageText()).build();
		List<AppleDevice> devices = deviceRepository.findByUser(e.getNotification().getUser());
		devices.forEach(d -> service.push(d.getToken().toUpperCase(), payload));
	}
}
