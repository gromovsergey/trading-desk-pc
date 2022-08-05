package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.site.TagOptionValue;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TagOptionValueAuditSerializer extends AbstractEntityAuditSerializer<TagOptionValue> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, TagOptionValue value) throws XMLStreamException {
        writer.writeAttribute("name", value.getOption().getName().getDefaultName());
    }
}