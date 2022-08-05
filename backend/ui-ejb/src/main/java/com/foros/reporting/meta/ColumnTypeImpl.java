package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.Order;

import java.io.Serializable;

public class ColumnTypeImpl implements ColumnType, Serializable {

    private String type;
    private Order defaultOrder;

    public ColumnTypeImpl(String type) {
        this(type, Order.ASC);
    }

    public ColumnTypeImpl(String type, Order defaultOrder) {
        this.type = type;
        this.defaultOrder = defaultOrder;
    }

    public String getName() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnTypeImpl that = (ColumnTypeImpl) o;

        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public Order getDefaultOrder() {
        return defaultOrder;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
