package com.foros.audit.serialize.serializer;

import com.foros.changes.inspection.ChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class NullAuditSerializer implements AuditSerializer {
    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
    }

    @Override
    public void endSerialize(XMLStreamWriter writer) throws XMLStreamException {
    }
}
