package com.foros.changes.inspection.changeNode.custom;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.CollectionAuditSerializer;
import com.foros.audit.serialize.serializer.CollectionItemAuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.changes.inspection.changeNode.DummyChangeNode;
import com.foros.changes.inspection.changeNode.EntityChange;
import com.foros.changes.inspection.changeNode.FieldChange;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.opportunity.OpportunityService;
import com.foros.util.CollectionUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public  class OpportunityEntityChange extends EntityChange {
    private static final AuditSerializer COLLECTION_SERIALIZER = new CollectionAuditSerializer();
    private static final AuditSerializer ITEM_SERIALIZER = new CollectionItemAuditSerializer();

    private Set<ChangeNode> changedFiles;

    public OpportunityEntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        super.prepareInternal(context);

        Opportunity opp = (Opportunity) object;

        Collection<String> added = CollectionUtils.safe(opp.getProperty(OpportunityService.ADDED_IO_FILES));

        Collection<String> removed = CollectionUtils.safe(opp.getProperty(OpportunityService.REMOVED_IO_FILES));

        if (changeType == ChangeType.UNCHANGED && (!removed.isEmpty() || !added.isEmpty())) {
            changeType = ChangeType.UPDATE;
        }

        changedFiles = new LinkedHashSet<ChangeNode>();

        for (String file : added) {
            changedFiles.add(new DummyChangeNode<String>(file, ChangeType.ADD));
        }

        for (String file : removed) {
            changedFiles.add(new DummyChangeNode<String>(file, ChangeType.REMOVE));
        }
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        AuditSerializer serializer = descriptor.getSerializer();
        serializer.startSerialize(writer, this);

        for (FieldChange change : changes) {
            if (change != null) {
                change.serialize(writer);
            }
        }

        if (!CollectionUtils.isNullOrEmpty(changedFiles)) {
            writer.writeStartElement("property");
            writer.writeAttribute("name", "attachment");
            COLLECTION_SERIALIZER.startSerialize(writer, null);
            for (ChangeNode itemChange : changedFiles) {
                ITEM_SERIALIZER.startSerialize(writer, itemChange);
                itemChange.serialize(writer);
                ITEM_SERIALIZER.endSerialize(writer);
            }
            COLLECTION_SERIALIZER.endSerialize(writer);
            writer.writeEndElement();
        }
        serializer.endSerialize(writer);
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected OpportunityEntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new OpportunityEntityChange(descriptor, object, changeType, changes);
        }
    }
}