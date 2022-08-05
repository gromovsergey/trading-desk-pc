package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.feed.Feed;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FeedAuditSerializer extends AbstractEntityAuditSerializer<Feed> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, Feed value) throws XMLStreamException {
        writer.writeAttribute("url", value.getUrl());
    }

}