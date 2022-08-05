package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.creative.CreativeCategory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CreativeCategoryAuditSerializer extends AbstractEntityAuditSerializer<CreativeCategory> {

    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, CreativeCategory value) throws XMLStreamException {
        super.writeAttributes(writer, node, value);
        writer.writeAttribute("name", value.getName().getDefaultName());
        writer.writeAttribute("type", value.getType().getName());
    }
}
