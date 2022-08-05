package com.foros.audit.serialize.serializer.primitive;

import com.foros.audit.serialize.serializer.AuditSerializerSupport;
import com.foros.changes.inspection.ChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class HiddenPropertyAuditSerializer extends AuditSerializerSupport {
    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
        writer.writeAttribute("hidden", Boolean.TRUE.toString());
    }

    @Override
    public void endSerialize(XMLStreamWriter writer) throws XMLStreamException {

    }
}
