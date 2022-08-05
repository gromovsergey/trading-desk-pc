package app.programmatic.ui.common.config;

import app.programmatic.ui.common.datasource.EmptyDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;
import javax.sql.DataSource;


@Configuration
@EnableConfigurationProperties({ BigDataConfig.BigDataSourceSettings.class })
public class BigDataConfig {

    @Bean(name = "bigDataSource")
    public DataSource configureDataSource(BigDataSourceSettings settings) {
        if (!settings.isAvailable()) {
            return new EmptyDataSource();
        }

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
        Properties properties = new Properties();
        properties.setProperty("socket_timeout", String.valueOf(settings.getQueryTimeout()));
        properties.setProperty("connection_timeout", String.valueOf(settings.getQueryTimeout()));
        config.setDataSourceProperties(properties);

        return new HikariDataSource(config);
    }

    @Bean(name = "bigDataOperations")
    public JdbcOperations configureJdbcOperations(@Qualifier("bigDataSource") DataSource dataSource,
                                                  BigDataSourceSettings settings) {
        return new JdbcTemplate(dataSource);
    }

    @ConfigurationProperties("bigdata.datasource")
    public static class BigDataSourceSettings {
        private boolean available;
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private int minIdlePoolSize;
        private int maxPoolSize;
        private String initSql;
        private String connectionTestQuery;
        private int queryTimeout;

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

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
}
