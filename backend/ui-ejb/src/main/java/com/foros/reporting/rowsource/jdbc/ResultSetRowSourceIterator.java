package com.foros.reporting.rowsource.jdbc;

import com.foros.reporting.ReportingException;
import com.foros.reporting.Row;
import com.foros.reporting.meta.DbColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

class ResultSetRowSourceIterator implements Iterator<Row> {

    private ResultSet resultSet;

    private boolean nextCalled = false;
    private boolean hasNext = false;

    private Row<DbColumn> row;

    ResultSetRowSourceIterator(ResultSet resultSet, ResultSetValueReaderRegistry readerRegistry) {
        this.resultSet = resultSet;
        this.row = new ResultSetRow(resultSet, readerRegistry);
    }

    @Override
    public boolean hasNext() {
        if (nextCalled) {
            return hasNext;
        }

        try {
            hasNext = resultSet.next();
            nextCalled = true;
            return hasNext;
        } catch (SQLException e) {
            throw new ReportingException(e);
        }
    }

    @Override
    public Row<DbColumn> next() {
        try {
            if (!nextCalled) {
                resultSet.next();
            }
            nextCalled = false;
        } catch (SQLException e) {
            throw new ReportingException(e);
        }

        return row;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Method not implemented");
    }

}
