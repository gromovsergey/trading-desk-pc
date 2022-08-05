package com.foros.action.admin.permissions;

import com.foros.util.StringUtil;

public class Parameter implements Comparable<Parameter> {

    private static final Parameter NULL_PARAMETER = new Parameter(null, null);

    private String name;
    private String text;

    public Parameter(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(Parameter o) {
        if (this == o || this.getText() == o.getText()) {
            return 0;
        }

        if (this.getText() == null) {
            return 1;
        }

        if (o.getText() == null) {
            return -1;
        }

        return StringUtil.compareToIgnoreCase(this.getText(), o.getText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (!name.equals(parameter.name)) return false;
        if (!text.equals(parameter.text)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
    
    public static Parameter nullParameter() {
        return NULL_PARAMETER;
    }

}
