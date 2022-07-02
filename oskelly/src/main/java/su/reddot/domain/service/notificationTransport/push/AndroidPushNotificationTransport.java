package su.reddot.domain.service.notificationTransport.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import su.reddot.domain.dao.device.AndroidDeviceRepository;
import su.reddot.domain.model.device.AndroidDevice;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.notification.NotificationView;
import su.reddot.domain.service.notificationTransport.NotificationTransport;
import su.reddot.domain.service.notificationTransport.NotificationTransportEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Khludeev on 05.02.18.
 */
@Component
@RequiredArgsConstructor
public class AndroidPushNotificationTransport implements NotificationTransport {

	private final AndroidDeviceRepository deviceRepository;
	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;

	@Value("${app.push.android.endpoint}")
	@Setter
	private String endpoint;

	@Value("${app.push.android.key}")
	@Setter
	private String key;

	@Override
	public void send(NotificationTransportEvent e) {
		List<AndroidDevice> devices = deviceRepository.findByUser(e.getNotification().getUser());
		RestTemplate restTemplate = new RestTemplate();
		devices.forEach(d -> {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json;charset=UTF-8");
			headers.add("Authorization", "key=" + key);

			Map<String, Object> map = new HashMap<>();
			map.put("to", d.getToken());
			NotificationView notificationView = notificationService.of(e.getNotification(), false, null);
			map.put("data", notificationView);

			HttpEntity<String> requestEntity;
			try {
				requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(map), headers);
			} catch (JsonProcessingException ex) {
				throw new IllegalArgumentException(String.format("Cannot parse JSON for notification with id %s", notificationView.getId()), ex);
			}
			restTemplate.postForObject(endpoint, requestEntity, String.class);
		});
	}
}