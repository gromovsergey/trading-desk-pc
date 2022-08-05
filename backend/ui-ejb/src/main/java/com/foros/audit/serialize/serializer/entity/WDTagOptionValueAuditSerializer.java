package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.site.WDTagOptionValue;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class WDTagOptionValueAuditSerializer extends AbstractEntityAuditSerializer<WDTagOptionValue> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, WDTagOptionValue value) throws XMLStreamException {
        writer.writeAttribute("name", value.getOption().getName().getDefaultName());
    }
}