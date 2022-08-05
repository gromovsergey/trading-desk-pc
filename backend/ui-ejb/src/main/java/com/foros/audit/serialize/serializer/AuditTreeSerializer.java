package com.foros.audit.serialize.serializer;

import com.foros.changes.inspection.ChangeNode;
import com.foros.model.security.ActionType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class AuditTreeSerializer extends AuditSerializerSupport {

    private static final String SCHEMA_VERSION = "1.1";

    private ActionType actionType;

    public AuditTreeSerializer(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
        writer.writeStartElement("auditRecord");
        writer.writeAttribute("version", SCHEMA_VERSION);
        writer.writeAttribute("type", actionType.getName());
    }
}
