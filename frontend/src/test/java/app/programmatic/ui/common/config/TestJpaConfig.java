package app.programmatic.ui.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@TestConfiguration
@EnableTransactionManagement(order = Ordered.HIGHEST_PRECEDENCE)
@EnableJpaRepositories("app.programmatic.ui")
@EnableConfigurationProperties({ JpaConfig.DataSourceSettings.class })
public class TestJpaConfig {
    private final String url = "jdbc:postgresql://dev03:5432/stat";
    private final String username = "ui";
    private final String password = "adserver";

    private JpaConfig jpaConfig = new JpaConfig();


    @Primary
    @Bean(name = "dataSource")
    public DataSource configureDataSource(JpaConfig.DataSourceSettings settings) {
        updateSettings(settings);

        return jpaConfig.configureDataSource(settings);
    }

    @Primary
    @Bean(name = "jdbcOperations")
    public JdbcOperations configureJdbcOperations(@Qualifier("dataSource") DataSource dataSource) {
        return jpaConfig.configureJdbcOperations(dataSource);
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaConfig.DataSourceSettings settings,
                                                                       @Qualifier("dataSource") DataSource dataSource) {
        return jpaConfig.entityManagerFactory(settings, dataSource);
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return jpaConfig.transactionManager();
    }

    @Bean(name = "annotationDrivenTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return jpaConfig.annotationDrivenTransactionManager();
    }

    private void updateSettings(JpaConfig.DataSourceSettings settings) {
        settings.setUrl(url);
        settings.setUsername(username);
        settings.setPassword(password);
    }
}
