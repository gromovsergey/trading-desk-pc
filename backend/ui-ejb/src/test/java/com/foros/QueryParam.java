package com.foros;

public class QueryParam {
    private String name;
    private Object value;
    private boolean isEqual;

    public QueryParam(String name, Object value) {
        this(name, value, true);
    }

    public QueryParam(String name, Object value, boolean isEqual) {
        this.name = name;
        this.value = value;
        this.isEqual = isEqual;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEqual() {
        return isEqual;
    }
}
