package com.foros.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Context implements Serializable {
    private Map<ExtensionProperty, Object> properties;

    public <T> T getProperty(ExtensionProperty<T> property) {
        if (properties == null) {
            return null;
        }

        return property.cast(properties.get(property));
    }

    public <T> T setProperty(ExtensionProperty<T> property, T value) {
        if (value == null) {
            return removeProperty(property);
        } else {
            if (properties == null) {
                properties = new HashMap<ExtensionProperty, Object>();
            }

            return property.cast(properties.put(property, value));
        }
    }

    public <T> T removeProperty(ExtensionProperty<T> property) {
        if (properties == null) {
            return null;
        }

        return property.cast(properties.remove(property));
    }

    public void clearProperties() {
        properties = null;
    }

}
