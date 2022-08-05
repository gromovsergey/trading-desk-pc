package com.foros.changes.inspection.changeNode.custom;

import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeNodeSupport;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.changes.inspection.changeNode.CollectionFieldChange;
import com.foros.changes.inspection.changeNode.CollectionItemChange;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CollectionStringValueChange extends CollectionFieldChange {

    protected CollectionStringValueChange(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    protected void addItemChange(PrepareChangesContext context, Object item, ChangeType ct) {
        itemChanges.add(new CollectionStringValueChangeNode(String.valueOf(item), ct));
    }

    public static class Factory extends CollectionFieldChange.Factory {
        @Override
        protected CollectionStringValueChange newInstanceInternal(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
            if (newValue != null && oldValue != null && 
                    newValue.containsAll(oldValue) && oldValue.containsAll(newValue)) {
                return null;
            }
            return new CollectionStringValueChange(descriptor, oldValue, newValue);
        }
    }

    private static class CollectionStringValueChangeNode extends ChangeNodeSupport {

        private String value;
        private ChangeType changeType;

        private CollectionStringValueChangeNode(String value, ChangeType changeType) {
            this.value = value;
            this.changeType = changeType;
        }

        @Override
        public Object getLastDefinedValue() {
            return value;
        }

        @Override
        public Iterator<? extends ChangeNode> getChildNodes() {
            return Collections.<ChangeNode>emptyList().iterator();
        }

        @Override
        public ChangeType getChangeType() {
            return changeType;
        }

        @Override
        public void serialize(XMLStreamWriter writer) throws XMLStreamException {
            CollectionItemChange.SERIALIZER.startSerialize(writer, this);
            writer.writeCData(value);
            CollectionItemChange.SERIALIZER.endSerialize(writer);
        }

        @Override
        public void prepareInternal(PrepareChangesContext context) {
        }
    }
}
