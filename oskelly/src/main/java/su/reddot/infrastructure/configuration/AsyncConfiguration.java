package su.reddot.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    /** Если не настроить пользотельский executor, будет использоваться
     *  {@link org.springframework.core.task.SimpleAsyncTaskExecutor},
     *  который создает новый поток на каждое задание, не переиспользуя эти потоки.
     *  В документации его использовать не рекомендует. */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        /* значения как в документации */
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("default-pool-");

        return executor;
    }
}
