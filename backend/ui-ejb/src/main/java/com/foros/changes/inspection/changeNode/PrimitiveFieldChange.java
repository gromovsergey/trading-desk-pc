package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang.ObjectUtils;

public class PrimitiveFieldChange extends FieldChange<Object> {

    public static final Iterator<ChangeNode> EMPTY_ITERATOR = Collections.<ChangeNode>emptyList().iterator();

    public PrimitiveFieldChange(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        super.prepareInternal(context);
        if (changeType == ChangeType.UNCHANGED) {
            changeType = ChangeType.UPDATE;
        }
    }


    @Override
    protected void serializeContent(XMLStreamWriter writer) throws XMLStreamException {
        AuditSerializer serializer = descriptor.getSerializer();
        serializer.startSerialize(writer, this);
        serializer.endSerialize(writer);
    }

    @Override
    public Iterator<ChangeNode> getChildNodes() {
        return EMPTY_ITERATOR;
    }

    public static class Factory implements FieldChangeFactory {
        @Override
        public FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            if ("".equals(oldValue)) {
                oldValue = null;
            }

            if ("".equals(newValue)) {
                newValue = null;
            }

            if (ObjectUtils.equals(newValue, oldValue)) {
                return null;
            }
            return new PrimitiveFieldChange(descriptor, oldValue, newValue);
        }
    }
}