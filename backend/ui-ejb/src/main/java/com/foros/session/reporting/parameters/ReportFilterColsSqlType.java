package com.foros.session.reporting.parameters;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class ReportFilterColsSqlType implements SQLData {

    private String columnName;
    private String operation;
    private String value;

    public ReportFilterColsSqlType(String columnName, String operation, String value) {
        this.columnName = columnName;
        this.operation = operation;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return "REPORT_FILTER_COLS_TYP";
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(columnName);
        stream.writeString(operation);
        stream.writeString(value);
    }

    @Override
    public String toString() {
        return columnName + operation + value;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {}
}
