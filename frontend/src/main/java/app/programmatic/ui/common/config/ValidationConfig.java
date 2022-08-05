package app.programmatic.ui.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;


@Configuration
@ComponentScan({"app.programmatic.ui.flight.service", "app.programmatic.ui.user.service"})
@EnableAspectJAutoProxy
public class ValidationConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());

        return methodValidationPostProcessor;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

        return localValidatorFactoryBean;
    }
}
