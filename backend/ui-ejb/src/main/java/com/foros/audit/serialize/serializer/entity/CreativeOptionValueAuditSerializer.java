package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.creative.CreativeOptionValue;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CreativeOptionValueAuditSerializer extends AbstractEntityAuditSerializer<CreativeOptionValue> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, CreativeOptionValue value) throws XMLStreamException {
        writer.writeAttribute("name", value.getOption().getName().getDefaultName());
    }
}
