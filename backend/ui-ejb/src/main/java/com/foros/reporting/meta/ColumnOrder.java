package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.Order;

public class ColumnOrder<C extends Column> {

    private C column;
    private Order order;

    public ColumnOrder(C column, Order order) {
        this.order = order;
        this.column = column;
    }

    public C getColumn() {
        return column;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return column + " " + order;
    }
}
