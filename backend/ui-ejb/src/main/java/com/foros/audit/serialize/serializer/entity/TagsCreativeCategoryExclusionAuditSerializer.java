package com.foros.audit.serialize.serializer.entity;

import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.site.TagsCreativeCategoryExclusion;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TagsCreativeCategoryExclusionAuditSerializer extends AbstractEntityAuditSerializer<TagsCreativeCategoryExclusion> {
    @Override
    protected void writeAttributes(XMLStreamWriter writer, EntityChangeNode node, TagsCreativeCategoryExclusion value) throws XMLStreamException {
        writer.writeAttribute("categoryId", String.valueOf(value.getTagsCreativeCategoryExclusionPK().getCreativeCategoryId()));
        writer.writeAttribute("categoryName", value.getCreativeCategory().getDefaultName());
        writer.writeAttribute("tagId", String.valueOf(value.getTagsCreativeCategoryExclusionPK().getTagId()));
    }
}