package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.Order;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class SqlColumnOrder implements SQLData {

    private String column;
    private Order order;

    public SqlColumnOrder(String column, Order order) {
        this.column = column;
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return "REPORT_SORT_COLS_TYP";
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(column);
        stream.writeString(order.name());
    }

    @Override
    public String toString() {
        return column + " " + order;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {}
}
