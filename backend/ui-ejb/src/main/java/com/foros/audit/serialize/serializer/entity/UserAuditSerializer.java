package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.security.User;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class UserAuditSerializer extends AbstractEntityAuditSerializer<User> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, User user) throws XMLStreamException {
        super.writeAttributes(writer, node, user);

        writer.writeAttribute("login", user.getEmail());
        writer.writeAttribute("firstName", user.getFirstName());
        writer.writeAttribute("lastName", user.getLastName());
    }
}