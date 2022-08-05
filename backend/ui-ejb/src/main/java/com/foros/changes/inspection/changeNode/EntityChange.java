package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.FieldAuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeNodeSupport;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.FilterIterator;

public class EntityChange extends ChangeNodeSupport implements EntityChangeNode {
    public static final FieldAuditSerializer FIELD_AUDIT_SERIALIZER = new FieldAuditSerializer();

    protected final EntityChangeDescriptor descriptor;
    protected final Object object;
    protected final FieldChange[] changes;

    protected ChangeType changeType;

    public EntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        this.descriptor = descriptor;
        this.changeType = changeType;
        this.object = object;
        this.changes = changes;
    }

    public void merge(EntityChangeNode otherNode) {
        EntityChange other = (EntityChange) otherNode;
        if (this.changeType == ChangeType.ADD && other.changeType == ChangeType.UNCHANGED) {
            // result is ADD
        } else if (this.changeType == ChangeType.UNCHANGED && other.changeType == ChangeType.UNCHANGED) {
            // result is UPDATE
        } else {
            throw new IllegalArgumentException(
                    "this.changeType=" + this.changeType +
                    ", id=" + getId() +
                    ", other.changeType=" + other.changeType +
                    ", descriptor=" + descriptor
            );
        }

        for (int i = 0; i < changes.length; i++) {
            FieldChange change = changes[i];
            FieldChange otherChange = other.changes[i];

            if (change == null) {
                changes[i] = otherChange;
            } else if (otherChange != null) {
                change.merge(otherChange);
            }
        }
    }

    public void prepareInternal(PrepareChangesContext context) {
        for (int i = 0; i < changes.length; i++) {
            FieldChange change = changes[i];
            if (change == null) {
                continue;
            }

            change.prepare(context);
            if (change.getChangeType() == ChangeType.UNCHANGED) {
                changes[i] = null;
            } else if (changeType == ChangeType.UNCHANGED) {
                changeType = ChangeType.UPDATE;
            }
        }
    }

    @Override
    public Object getLastDefinedValue() {
        return object;
    }

    @Override
    public Iterator<ChangeNode> getChildNodes() {
        //noinspection unchecked
        return new FilterIterator(new ArrayIterator(changes), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object != null;
            }
        });
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        AuditSerializer serializer = descriptor.getSerializer();
        serializer.startSerialize(writer, this);
        for (FieldChange change : changes) {
            if (change != null) {
                change.serialize(writer);
            }
        }
        serializer.endSerialize(writer);
    }

    public EntityChangeDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public Class<?> getType() {
        return descriptor.getType();
    }

    @Override
    public Object getId() {
        return descriptor.getId(object);
    }

    @Override
    public String getIdProperty() {
        return descriptor.getIdProperty();
    }

    @Override
    public Long geAuditLogId() {
        return (Long) getId();
    }

    public static class Factory implements EntityChangeFactory {
        @Override
        public final EntityChange newInstance(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return newInstanceInternal(descriptor, object, changeType, changes);
        }

        protected EntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new EntityChange(descriptor, object, changeType, changes);
        }
    }
}
