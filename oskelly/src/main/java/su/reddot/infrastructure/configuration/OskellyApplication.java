package su.reddot.infrastructure.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import su.reddot.infrastructure.acquirer.impl.mdm.MdmPayConfiguration;
import su.reddot.infrastructure.cashregister.impl.starrys.StarrysConfiguration;
import su.reddot.infrastructure.export.vk.VkConfiguration;

@SpringBootApplication(scanBasePackages = "su.reddot")
@Import({SecurityConfiguration.class})
@EnableJpaRepositories(basePackages = {"su.reddot.domain.dao", "su.reddot.infrastructure"})
@EntityScan(basePackages = {"su.reddot.domain.model", "su.reddot.infrastructure"})
@EnableTransactionManagement
@EnableConfigurationProperties({
        MdmPayConfiguration.class,
        StarrysConfiguration.class,
        VkConfiguration.class
})
public class OskellyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OskellyApplication.class, args);
    }
}
