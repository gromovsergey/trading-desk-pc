package com.foros.cache.generic.model;

import java.util.List;

public class SomeEntity {

    private List<String> stringsField;

    private Integer intField;

    public List<String> getStringsField() {
        return stringsField;
    }

    public void setStringsField(List<String> stringsField) {
        this.stringsField = stringsField;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    @Override
    public String toString() {
        return "SomeEntity{" +
                "stringsField='" + stringsField + '\'' +
                ", intField=" + intField +
                '}';
    }
}
