package com.foros.session.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EjbSupportConfiguration {

    @Bean
    public static BeanFactoryPostProcessor ejbBeanPostProcessor() {
        return new EjbBeansPostProcessor();
    }

}
