package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.site.SiteCreativeCategoryExclusion;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SiteCreativeCategoryExclusionAuditSerializer extends AbstractEntityAuditSerializer<SiteCreativeCategoryExclusion> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, SiteCreativeCategoryExclusion value) throws XMLStreamException {
        writer.writeAttribute("categoryId", String.valueOf(value.getSiteCreativeCategoryExclusionPK().getCreativeCategoryId()));
        writer.writeAttribute("categoryName", value.getCreativeCategory().getDefaultName());
        writer.writeAttribute("categoryType", value.getCreativeCategory().getType().getName());
    }
}