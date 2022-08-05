package com.foros.reporting.rowsource.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetValueReader<T> {

    T readValue(ResultSet resultSet, String name) throws SQLException;

}
