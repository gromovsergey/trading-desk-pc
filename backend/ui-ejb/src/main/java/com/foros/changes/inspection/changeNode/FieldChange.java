package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.FieldAuditSerializer;
import com.foros.changes.inspection.ChangeNodeSupport;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class FieldChange<T> extends ChangeNodeSupport {
    public static final AuditSerializer SERIALIZER = new FieldAuditSerializer();

    protected final FieldChangeDescriptor descriptor;
    protected final T oldValue;
    protected T newValue;
    protected ChangeType changeType = ChangeType.UNCHANGED;

    protected abstract void serializeContent(XMLStreamWriter writer) throws XMLStreamException;

    protected FieldChange(FieldChangeDescriptor descriptor, T oldValue, T newValue) {
        this.descriptor = descriptor;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public void prepareInternal(PrepareChangesContext context) {
        if (newValue == null) {
            changeType = ChangeType.REMOVE;
        } else if (oldValue == null) {
            changeType = ChangeType.ADD;
        }
    }

    public String getPropertyName() {
        return descriptor.getName();
    }

    @Override
    public Object getLastDefinedValue() {
        return changeType == ChangeType.REMOVE ? oldValue : newValue;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public final void serialize(XMLStreamWriter writer) throws XMLStreamException {
        SERIALIZER.startSerialize(writer, this);
        serializeContent(writer);
        SERIALIZER.endSerialize(writer);
    }

    public void merge(FieldChange<T> change) {
        this.newValue = change.newValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[changeType=" + getChangeType() + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
    }
}
