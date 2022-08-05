package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.CollectionItemAuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeNodeSupport;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.PrepareChangesContext;

import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CollectionItemChange extends ChangeNodeSupport {
    public static final AuditSerializer SERIALIZER = new CollectionItemAuditSerializer();
    private ChangeType changeType;
    private EntityChangeNode entityChange;

    public CollectionItemChange(ChangeType changeType, EntityChangeNode entityChange) {
        this.changeType = changeType;
        this.entityChange = entityChange;
    }

    @Override
    public Object getLastDefinedValue() {
        return entityChange.getLastDefinedValue();
    }

    @Override
    public Iterator<? extends ChangeNode> getChildNodes() {
        return Collections.singleton(entityChange).iterator();
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        SERIALIZER.startSerialize(writer, this);
        entityChange.serialize(writer);
        SERIALIZER.endSerialize(writer);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
    }
}
