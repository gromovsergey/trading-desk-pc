package com.foros.cache.generic.model;

import java.util.List;

public class SomeKey {

    private List<String> stringField;

    private Long parentId;

    public List<String> getStringField() {
        return stringField;
    }

    public void setStringField(List<String> stringField) {
        this.stringField = stringField;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Key{" + stringField + "|" + parentId + '}';
    }

}
