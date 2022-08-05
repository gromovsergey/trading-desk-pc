package com.foros.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * WARNING: Extension properties can not be used in remote call. They can be used in only in one JVM.
 * @param <T> property type
 */
public class ExtensionProperty<T> implements Serializable {

    private final UUID id;
    private final Class<T> propertyType;

    public ExtensionProperty(Class<T> propertyType) {
        this.id = UUID.randomUUID();
        this.propertyType = propertyType;
    }

    public T cast(Object obj) {
        if (obj == null) {
            return null;
        }
        if (!propertyType.isAssignableFrom(obj.getClass())) {
            throw new ClassCastException("Wrong property value");
        }
        return propertyType.cast(obj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtensionProperty that = (ExtensionProperty) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + "<" + propertyType.getSimpleName() + ">";
    }
}
