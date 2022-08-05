package com.foros.migration;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class Migration {

    public interface Executor {
        void run() throws Exception;
    }

    private static final int SEVERE_ERROR_EXIT_CODE = 1;

    private static final Logger logger = Logger.getLogger(Migration.class.getName());

    @Bean
    @Lazy
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setLocations(new Resource[] { new PathResource(System.getenv("COLOCATION_PROPERTIES_FILE")) });
        return properties;
    }

    @Bean
    public Logger getLogger() {
        return logger;
    }

    @Bean
    public JdbcTemplate postgresJdbcTemplate(
            @Value("${pg_url}") String url,
            @Value("${pg_user}") String username,
            @Value("${pg_password}") String password
            ) {
        return new JdbcTemplate(new DriverManagerDataSource(url, username, password), true);
    }

    public static void perform(Class migrationConfig) {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Migration.class, migrationConfig)) {

            Executor executor = context.getBean(Executor.class);
            executor.run();

        } catch (Exception e) {
            logger.severe("Error during migration: " + e.getMessage());
            e.printStackTrace();
            System.exit(SEVERE_ERROR_EXIT_CODE);
        }
    }
}
