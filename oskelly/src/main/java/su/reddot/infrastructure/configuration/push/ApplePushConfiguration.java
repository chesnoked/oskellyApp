package su.reddot.infrastructure.configuration.push;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Vitaliy Khludeev on 02.02.18.
 */
@Configuration
@EnableConfigurationProperties({ApplePushNotificationConfiguration.class})
@RequiredArgsConstructor
@Slf4j
public class ApplePushConfiguration {

	@Bean
	public ApnsService apnsService(ApplePushNotificationConfiguration c) {
		ApnsService service;
		try {
			service = APNS.newService()
					.withCert(c.getCertificatePath(), c.getCertificatePassword())
					.withProductionDestination()
					.build();
		}
		catch (Exception e) {
			log.error(e.getLocalizedMessage(), new IllegalStateException("Не удалось создать Bean ApnsService", e));
			return null;
		}
		return service;
	}
}
