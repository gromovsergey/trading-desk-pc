package com.foros.audit.serialize.serializer.entity;

import com.foros.audit.serialize.serializer.AuditSerializerSupport;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.changeNode.EntityChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractEntityAuditSerializer<T> extends AuditSerializerSupport {

    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode node) throws XMLStreamException {
        writer.writeStartElement("entity");

        EntityChangeNode entityChange = (EntityChangeNode) node;
        writer.writeAttribute("class", entityChange.getType().getName());

        @SuppressWarnings({"unchecked"})
        T value = (T) entityChange.getLastDefinedValue();

        writeAttributes(writer, entityChange, value);
    }

    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, T value) throws XMLStreamException {
        String idProperty = node.getIdProperty();
        if (idProperty != null) {
            writer.writeAttribute(idProperty, String.valueOf(node.getId()));
        }
    }
}