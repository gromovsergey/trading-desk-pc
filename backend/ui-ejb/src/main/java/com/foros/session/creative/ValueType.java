package com.foros.session.creative;

import com.foros.reporting.meta.ColumnType;

import java.io.Serializable;

public class ValueType implements Serializable {
    private Object value;
    private ColumnType type;

    public ValueType(Object value, ColumnType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void clearValue() {
        this.value = null;
    }

    public ColumnType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ValueType(" + value + ", " + type + ")";
    }
}
