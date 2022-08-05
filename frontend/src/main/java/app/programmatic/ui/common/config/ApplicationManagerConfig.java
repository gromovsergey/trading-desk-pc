package app.programmatic.ui.common.config;

import app.programmatic.ui.authentication.service.LdapServiceConfig;
import app.programmatic.ui.common.foros.service.SourceServiceConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;


@Lazy
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@Import({
        SourceServiceConfig.class,
        JpaConfig.class,
        BigDataConfig.class,
        LdapServiceConfig.class,
        WebMvcConfig.class,
        ValidationConfig.class,
        WorkerConfig.class
})
public class ApplicationManagerConfig {
}
