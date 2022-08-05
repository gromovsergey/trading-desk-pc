package com.foros.audit.serialize.serializer;

import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.changeNode.FieldChange;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FieldAuditSerializer extends AuditSerializerSupport {
    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
        writer.writeStartElement("property");
        writer.writeAttribute("name", ((FieldChange) node).getPropertyName());
        writer.writeAttribute("changeType", node.getChangeType().toString());
    }
}
