package app.programmatic.ui.common.config;

import static app.programmatic.ui.common.config.JpaConfig.getPoolConfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({ StatDataConfig.StatDataSourceSettings.class })
public class StatDataConfig {

    @Bean(name = "statDataSource")
    public DataSource configureDataSource(StatDataConfig.StatDataSourceSettings settings) {
        return new HikariDataSource(getPoolConfig(settings));
    }

    @Bean(name = "statDataOperations")
    public JdbcOperations configureJdbcOperations(@Qualifier("statDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @ConfigurationProperties("statdata.datasource")
    public static class StatDataSourceSettings extends JpaConfig.PgDataSourceSettings {
    }
}