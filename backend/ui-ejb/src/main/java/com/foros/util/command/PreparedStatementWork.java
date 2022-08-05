package com.foros.util.command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementWork extends AbstractPreparedStatementWork<Integer>{

    public PreparedStatementWork() {
        super();
    }

    public PreparedStatementWork(String sql) {
        super(sql);
    }

    @Override
    protected Integer execute(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }

    protected void prepareStatement(PreparedStatement statement) throws SQLException {
    }
}
