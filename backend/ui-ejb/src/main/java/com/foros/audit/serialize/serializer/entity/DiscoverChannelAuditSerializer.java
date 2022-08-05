package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.channel.DiscoverChannel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DiscoverChannelAuditSerializer extends AbstractEntityAuditSerializer<DiscoverChannel> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, DiscoverChannel value) throws XMLStreamException {
        super.writeAttributes(writer, node, value);
        writer.writeAttribute("name", value.getName());
        if (value.getChannelList() != null) {
            writer.writeAttribute("baseKeyword", value.getBaseKeyword());
        }
    }
}
