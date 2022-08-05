package com.foros.audit.serialize.serializer.primitive;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.ChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class PrimitiveAuditSerializer implements AuditSerializer {

    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
        Object value = node.getLastDefinedValue();
        if (value != null) {
            writer.writeCharacters(toString(value));
        }
    }

    protected String toString(Object value) {
        return String.valueOf(value);
    }

    @Override
    public void endSerialize(XMLStreamWriter writer) throws XMLStreamException {
    }
}