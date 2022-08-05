package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.site.TagPricing;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TagPricingAuditSerializer extends AbstractEntityAuditSerializer<TagPricing> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, TagPricing value) throws XMLStreamException {
        if (value.isDefault()) {
            writer.writeAttribute("countryCode", value.getCountry() != null ? value.getCountry().getCountryCode() : "default");
        } else {
            writer.writeAttribute("countryCode", value.getCountry() != null ? value.getCountry().getCountryCode() : "All");
            writer.writeAttribute("ccgType", value.getCcgType() != null ? value.getCcgType().toString(): "All");
            writer.writeAttribute("ccgRateType", value.getCcgRateType() != null ? value.getCcgRateType().toString(): "All");
        }
    }

}