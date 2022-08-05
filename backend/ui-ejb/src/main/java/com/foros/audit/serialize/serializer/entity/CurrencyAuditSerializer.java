package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.currency.Currency;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CurrencyAuditSerializer extends AbstractEntityAuditSerializer<Currency> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, Currency value) throws XMLStreamException {
        writer.writeAttribute("code", value.getCurrencyCode());
    }
}