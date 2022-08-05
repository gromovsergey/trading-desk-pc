package com.foros.audit.serialize.serializer;

import com.foros.changes.inspection.ChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface AuditSerializer {
    void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException;

    void endSerialize(XMLStreamWriter writer) throws XMLStreamException;
}
