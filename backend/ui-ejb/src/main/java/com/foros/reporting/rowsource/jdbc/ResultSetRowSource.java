package com.foros.reporting.rowsource.jdbc;

import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetRowSource implements RowSource {

    private ResultSet resultSet;
    private ResultSetValueReaderRegistry readerRegistry;

    public ResultSetRowSource(ResultSet resultSet, ResultSetValueReaderRegistry readerRegistry) throws SQLException {
        this.resultSet = resultSet;
        this.readerRegistry = readerRegistry;
    }

    @Override
    public Iterator<Row> iterator() {
        return new ResultSetRowSourceIterator(resultSet, readerRegistry);
    }
}
