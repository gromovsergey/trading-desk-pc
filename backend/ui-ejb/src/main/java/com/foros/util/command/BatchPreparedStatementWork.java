package com.foros.util.command;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public abstract class BatchPreparedStatementWork<T> extends AbstractPreparedStatementWork<int[]> {

    private Collection<T> values;

    public BatchPreparedStatementWork(Collection<T> values) {
        super();
        this.values = values;
    }

    public BatchPreparedStatementWork(String sql, Collection<T> values) {
        super(sql);
        this.values = values;
    }

    @Override
    protected int[] execute(PreparedStatement statement) throws SQLException {
        return statement.executeBatch();
    }

    @Override
    protected void prepareStatement(PreparedStatement statement) throws SQLException {
        for (T value : values) {
            set(statement, value);
            statement.addBatch();
        }
    }

    protected abstract void set(PreparedStatement statement, T value) throws SQLException;

}
