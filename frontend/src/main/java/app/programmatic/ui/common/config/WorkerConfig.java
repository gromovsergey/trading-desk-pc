package app.programmatic.ui.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableConfigurationProperties({ WorkerConfig.AppWorkerSettings.class })
@Configuration
public class WorkerConfig {

    @Bean(name = "mainAppWorker")
    public Executor asyncExecutor(AppWorkerSettings settings) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(settings.getCorePoolSize());
        executor.setMaxPoolSize(settings.getMaxPoolSize());
        executor.setQueueCapacity(settings.getQueueSize());
        executor.setThreadNamePrefix("AppWorker-");
        executor.initialize();
        return executor;
    }

    @ConfigurationProperties("appworker")
    public static class AppWorkerSettings {
        private Integer corePoolSize;
        private Integer maxPoolSize;
        private Integer queueSize;

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(Integer queueSize) {
            this.queueSize = queueSize;
        }
    }
}
