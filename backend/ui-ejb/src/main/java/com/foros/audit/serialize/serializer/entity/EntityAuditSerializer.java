package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.IdNameEntity;
import com.foros.model.LocalizableNameEntity;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class EntityAuditSerializer extends AbstractEntityAuditSerializer<Object> {

    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, Object value) throws XMLStreamException {
        super.writeAttributes(writer, node, value);

        if (value instanceof IdNameEntity) {
            writer.writeAttribute("name", ((IdNameEntity) value).getName());
        } else if (value instanceof LocalizableNameEntity) {
            writer.writeAttribute("name", ((LocalizableNameEntity) value).getName().getDefaultName());
        }
    }
}
