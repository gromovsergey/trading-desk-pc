package app.programmatic.ui.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Properties;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement(order = Ordered.HIGHEST_PRECEDENCE)
@EnableJpaRepositories("app.programmatic.ui")
@EnableConfigurationProperties({ JpaConfig.DataSourceSettings.class })
public class JpaConfig implements TransactionManagementConfigurer {

    @Primary
    @Bean(name = "dataSource")
    public DataSource configureDataSource(DataSourceSettings settings) {
        return new HikariDataSource(getPoolConfig(settings));
    }

    @Primary
    @Bean(name = "jdbcOperations")
    public JdbcOperations configureJdbcOperations(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSourceSettings settings,
                                                                       @Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("app.programmatic.ui");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults", settings.getHibernate().getUseJdbcMetadata());
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Override
    @Bean(name = "annotationDrivenTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }

    public static HikariConfig getPoolConfig(PgDataSourceSettings settings) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(settings.getDriverClassName());
        config.setJdbcUrl(settings.getUrl());
        config.setUsername(settings.getUsername());
        config.setPassword(settings.getPassword());

        config.setMinimumIdle(settings.getMinIdlePoolSize());
        config.setMaximumPoolSize(settings.getMaxPoolSize());
        config.setConnectionInitSql(settings.getInitSql());
        config.setConnectionTestQuery(settings.getConnectionTestQuery());

        config.setConnectionTimeout(settings.getQueryTimeout());

        return config;
    }

    @ConfigurationProperties("datasource")
    public static class DataSourceSettings extends PgDataSourceSettings {
    }

    public static class PgDataSourceSettings {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private HibernateConf hibernate;
        private int minIdlePoolSize;
        private int maxPoolSize;
        private String initSql;
        private String connectionTestQuery;
        private int queryTimeout;

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public HibernateConf getHibernate() {
            return hibernate;
        }

        public void setHibernate(HibernateConf hibernate) {
            this.hibernate = hibernate;
        }

        public int getMinIdlePoolSize() {
            return minIdlePoolSize;
        }

        public void setMinIdlePoolSize(int minIdlePoolSize) {
            this.minIdlePoolSize = minIdlePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public String getInitSql() {
            return initSql;
        }

        public void setInitSql(String initSql) {
            this.initSql = initSql;
        }

        public String getConnectionTestQuery() {
            return connectionTestQuery;
        }

        public void setConnectionTestQuery(String connectionTestQuery) {
            this.connectionTestQuery = connectionTestQuery;
        }

        public int getQueryTimeout() {
            return queryTimeout;
        }

        public void setQueryTimeout(int queryTimeout) {
            this.queryTimeout = queryTimeout;
        }
    }

    public static class HibernateConf {
        private Boolean useJdbcMetadata;

        public Boolean getUseJdbcMetadata() {
            return useJdbcMetadata;
        }

        public void setUseJdbcMetadata(Boolean useJdbcMetadata) {
            this.useJdbcMetadata = useJdbcMetadata;
        }
    }
}
