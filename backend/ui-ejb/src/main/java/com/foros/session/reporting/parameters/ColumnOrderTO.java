package com.foros.session.reporting.parameters;

public class ColumnOrderTO {
    private String column;
    private Order order;

    public ColumnOrderTO() {
    }

    public ColumnOrderTO(String column, Order order) {
        this.column = column;
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
