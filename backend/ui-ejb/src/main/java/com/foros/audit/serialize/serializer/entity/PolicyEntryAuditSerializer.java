package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.security.PolicyEntry;
import com.foros.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class PolicyEntryAuditSerializer extends AbstractEntityAuditSerializer<PolicyEntry> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, PolicyEntry value) throws XMLStreamException {
        writer.writeAttribute("type", value.getType());
        writer.writeAttribute("action", value.getAction());
        if (!StringUtil.isPropertyEmpty(value.getParameter())) {
            writer.writeAttribute("parameter", value.getParameter());
        }
    }
}
