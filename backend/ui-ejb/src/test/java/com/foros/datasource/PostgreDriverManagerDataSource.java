package com.foros.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class PostgreDriverManagerDataSource extends DriverManagerDataSource {

    private boolean autoCommit;

    @Override
    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        Connection connection = super.getConnectionFromDriverManager(url, props);
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

}
