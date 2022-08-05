package com.foros.changes.inspection;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.changes.inspection.changeNode.FieldChange;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface ChangeNode {
    Iterator<ChangeNode> EMPTY_CHANGES = Collections.<ChangeNode>emptyList().iterator();

    Object getLastDefinedValue();

    Iterator<? extends ChangeNode> getChildNodes();

    ChangeType getChangeType();

    void serialize(XMLStreamWriter writer) throws XMLStreamException;

    void prepare(PrepareChangesContext context);

    public interface Factory {
    }

    public interface FieldChangeFactory extends Factory {
        FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue);
    }

    public interface EntityChangeFactory extends Factory {
        EntityChangeNode newInstance(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes);
    }

    public static final class NullFactory implements FieldChangeFactory, EntityChangeFactory {
        @Override
        public EntityChangeNode newInstance(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return null;
        }

        @Override
        public FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            return null;
        }
    }
}
