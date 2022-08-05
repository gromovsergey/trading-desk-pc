package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.ctra.CTRAlgorithmAdvertiserExclusion;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CTRAlgorithmAdvertiserExclusionAuditSerializer extends AbstractEntityAuditSerializer<CTRAlgorithmAdvertiserExclusion> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, CTRAlgorithmAdvertiserExclusion value) throws XMLStreamException {
        writer.writeAttribute("advertiserId", String.valueOf(value.getPk().getAdvertiserId()));
    }
}
