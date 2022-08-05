package com.foros.action.admin.permissions;

public class PolicyEntryInfo {
    private String objectType;
    private String action;
    private String parameter;
    private Long entry;
    private Boolean value;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Long getEntry() {
        return entry;
    }

    public void setEntry(Long entry) {
        this.entry = entry;
    }

    public Boolean hasValue() {
        return value != null ? value : false;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
