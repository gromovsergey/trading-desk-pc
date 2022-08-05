package com.foros.util.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractPreparedStatementWork<T> extends AbstractConnectionHibernateWork<T>{

    protected String sql;

    public AbstractPreparedStatementWork() {
    }

    public AbstractPreparedStatementWork(String sql) {
        this.sql = sql;
    }

    @Override
    protected T execute(Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            prepareStatement(stmt);
            return execute(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    protected abstract T execute(PreparedStatement statement) throws SQLException;

    protected abstract void prepareStatement(PreparedStatement statement) throws SQLException;

}

