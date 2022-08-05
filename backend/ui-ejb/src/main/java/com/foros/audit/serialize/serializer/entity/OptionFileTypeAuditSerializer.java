package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.template.OptionFileType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class OptionFileTypeAuditSerializer extends AbstractEntityAuditSerializer<OptionFileType> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, OptionFileType value) throws XMLStreamException {
        writer.writeAttribute("fileType", String.valueOf(value.getFileType()));
    }
}
