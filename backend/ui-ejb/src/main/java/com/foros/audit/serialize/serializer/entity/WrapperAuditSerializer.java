package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class WrapperAuditSerializer extends AbstractEntityAuditSerializer {

    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, Object value) throws XMLStreamException {
        // surrogate entities has no attributes
    }
}
