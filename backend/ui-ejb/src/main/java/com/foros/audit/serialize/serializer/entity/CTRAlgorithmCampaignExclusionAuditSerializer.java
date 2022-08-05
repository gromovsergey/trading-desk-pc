package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.ctra.CTRAlgorithmCampaignExclusion;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CTRAlgorithmCampaignExclusionAuditSerializer extends AbstractEntityAuditSerializer<CTRAlgorithmCampaignExclusion> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, CTRAlgorithmCampaignExclusion value) throws XMLStreamException {
        writer.writeAttribute("campaignId", String.valueOf(value.getPk().getCampaignId()));
    }
}
