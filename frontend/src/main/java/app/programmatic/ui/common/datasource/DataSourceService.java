package app.programmatic.ui.common.datasource;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.concurrent.Callable;


public interface DataSourceService {
    <T> T executeWithAuth(JdbcOperations jdbcOperations, Callable<T> callable);

    void executeWithAuth(JdbcOperations jdbcOperations, Runnable runnable);
}
