package su.reddot.infrastructure.export.vk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app.integration.vk")
@Getter @Setter
public class VkConfiguration {

    private String accessToken;

    private String groupId;

    private Map<Long, Integer> albumByCategory;
}
