package com.foros.reporting.rowsource.jdbc;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.DbColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultSetRow implements Row<DbColumn> {

    private ResultSet resultSet;
    private ResultSetValueReaderRegistry resultSetValueReaderRegistry;
    private Map<DbColumn, ResultSetValueReader> cache = new HashMap<DbColumn, ResultSetValueReader>();

    public ResultSetRow(ResultSet resultSet, ResultSetValueReaderRegistry resultSetValueReaderRegistry) {
        this.resultSet = resultSet;
        this.resultSetValueReaderRegistry = resultSetValueReaderRegistry;
    }

    @Override
    public Object get(DbColumn column) {
        ResultSetValueReader reader = cache.get(column);
        if (reader == null) {
            reader = resultSetValueReaderRegistry.get(column);
            cache.put(column, reader);
        }

        try {
            return reader.readValue(resultSet, column.getResultSetName());
        } catch (SQLException e) {
            throw new RuntimeException("Can't read column: " + column.getResultSetName() + " for column " + column, e);
        }
    }

    @Override
    public RowType getType() {
        return RowTypes.data();
    }

}
