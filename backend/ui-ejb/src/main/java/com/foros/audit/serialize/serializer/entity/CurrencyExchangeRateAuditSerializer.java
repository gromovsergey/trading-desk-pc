package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.currency.CurrencyExchangeRate;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CurrencyExchangeRateAuditSerializer extends AbstractEntityAuditSerializer<CurrencyExchangeRate> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, CurrencyExchangeRate value) throws XMLStreamException {
        writer.writeAttribute("currency", value.getCurrency().getCurrencyCode());
    }

}