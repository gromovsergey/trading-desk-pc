package app.programmatic.ui.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

import app.programmatic.ui.common.foros.service.TestSourceServiceConfig;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Lazy
@TestConfiguration
@EnableAspectJAutoProxy
@ComponentScan("app.programmatic.ui")
@Import({
        TestSourceServiceConfig.class,
        TestJpaConfig.class,
        ValidationConfig.class
})
public class TestConfig {
}