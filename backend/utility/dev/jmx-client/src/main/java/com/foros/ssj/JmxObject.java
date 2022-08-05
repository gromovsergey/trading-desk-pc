package com.foros.ssj;

import java.util.Arrays;
import javax.management.ObjectName;

public class JmxObject {
    private final String propertyName;
    private final ObjectName objectName;
    private final String attribute;
    private String attributeParam;
    private String value;

    public JmxObject(String propertyName, String objectName, String attribute) {
        try {
            this.propertyName = propertyName;
            this.objectName = new ObjectName(objectName);
            this.attribute = attribute;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public String getAttributeParam() {
        return attributeParam;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setAttributeParam(String attributeParam) {
        this.attributeParam = attributeParam;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "JmxObject[propertyName=" + propertyName + ", objectName="
                + objectName + ", attribute=" + attribute + ", attributeParam="
                + attributeParam + ", value=" + value + "]";
    }

    public Object getCacheKey() {
        return Arrays.asList(objectName, attribute);
    }
}
