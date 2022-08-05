package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.Timezone;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TimezoneAuditSerializer extends AbstractEntityAuditSerializer<Timezone> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, Timezone value) throws XMLStreamException {
        writer.writeAttribute("key", value.getKey());
    }
}