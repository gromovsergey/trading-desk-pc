package com.foros.changes.inspection;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.changeNode.FieldChange;

import java.lang.reflect.Field;

public class FieldChangeDescriptor {
    private ChangeNode.FieldChangeFactory fieldChangeFactory;
    private boolean cascade;
    private AuditSerializer serializer;
    private Field field;

    public FieldChangeDescriptor(Field field, ChangeNode.FieldChangeFactory fieldChangeFactory, boolean cascade, AuditSerializer serializer) {
        this.field = field;
        this.field.setAccessible(true);
        this.fieldChangeFactory = fieldChangeFactory;
        this.cascade = cascade;
        this.serializer = serializer;
    }

    public FieldChange newInstance(Object oldValue, Object newValue) {
        return fieldChangeFactory.newInstance(this, oldValue, newValue);
    }

    public boolean isCascade() {
        return cascade;
    }

    public AuditSerializer getSerializer() {
        return serializer;
    }

    public String getName() {
        return field.getName();
    }

    @Override
    public String toString() {
        return "FieldChangeDescriptor[" + getName() + "]";
    }

    public Object getValue(Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
