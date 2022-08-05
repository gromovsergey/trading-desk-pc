package com.foros.session.reporting;

public class PreparedParameter {
    private String id;
    private String name;
    private String valueText;
    private Object originalValue;

    public PreparedParameter(String id, String name, String valueText, Object originalValue) {
        this.id = id;
        this.name = name;
        this.valueText = valueText;
        this.originalValue = originalValue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValueText() {
        return valueText;
    }

    public Object getOriginalValue() {
        return originalValue;
    }
}