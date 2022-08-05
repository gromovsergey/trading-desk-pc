package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Hibernate;

public class EntityFieldChange extends FieldChange<Object> {

    protected EntityChangeNode entityChange;

    public EntityFieldChange(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public Iterator<? extends ChangeNode> getChildNodes() {
        return Collections.singleton(entityChange).iterator();
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        super.prepareInternal(context);
        if (changeType == ChangeType.UNCHANGED && !ObjectUtils.equals(oldValue, newValue)) {
            changeType = ChangeType.UPDATE;
        }

        calculateChildrenChangeType(context);

        if (entityChange.getChangeType() == ChangeType.REMOVE) {
            changeType = ChangeType.REMOVE;
        } else if (changeType == ChangeType.UNCHANGED && entityChange.getChangeType() != ChangeType.UNCHANGED) {
            changeType = ChangeType.UPDATE;
        }
    }

    @Override
    protected void serializeContent(XMLStreamWriter writer) throws XMLStreamException {
        entityChange.serialize(writer);
    }

    private void calculateChildrenChangeType(PrepareChangesContext context) {
        Object value = getLastDefinedValue();

        if (descriptor.isCascade()) {
            entityChange = context.getChange(value);
            if (entityChange == null) {
                EntityChangeDescriptor descriptor = context.getDescriptor(value);
                entityChange = descriptor.newEntityChange(value);
            }

            entityChange.prepare(context);
        } else {
            entityChange = context.getDescriptor(value).newEmptyEntityChange(value);
        }
    }

    public static class Factory implements FieldChangeFactory {
        @Override
        public FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            if (newValue == null && oldValue == null) {
                return null;
            }

            // same uninitialized value means no changes
            if (newValue == oldValue && !Hibernate.isInitialized(newValue)) {
                return null;
            }

            if (!descriptor.isCascade() && ObjectUtils.equals(newValue, oldValue)) {
                return null;
            }

            return new EntityFieldChange(descriptor, oldValue, newValue);
        }
    }
}
