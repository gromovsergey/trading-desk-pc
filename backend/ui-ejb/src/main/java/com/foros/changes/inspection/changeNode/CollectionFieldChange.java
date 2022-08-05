package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.CollectionAuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.hibernate.Hibernate;
import org.hibernate.collection.PersistentCollection;

@SuppressWarnings({"unchecked"})
public class CollectionFieldChange extends FieldChange<Collection> {

    private static final AuditSerializer COLLECTION_AUDIT_SERIALIZER = new CollectionAuditSerializer();

    protected List<ChangeNode> itemChanges;

    protected CollectionFieldChange(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        super.prepareInternal(context);

        Set<?> oldValues = toSet(oldValue);
        Set<?> newValues = toSet(newValue);

        Set other = removeDuplicates(oldValues, newValues);

        int potentialChanges = oldValues.size() + newValues.size() + (descriptor.isCascade() ? other.size() : 0);
        if (potentialChanges == 0) {
            return;
        }

        itemChanges = new ArrayList<ChangeNode>(potentialChanges);

        for (Object item : oldValues) {
            addItemChange(context, item, ChangeType.REMOVE);
        }

        for (Object item : newValues) {
            addItemChange(context, item, ChangeType.ADD);
        }

        if (descriptor.isCascade()) {
            for (Object item : other) {
                addItemChange(context, item, ChangeType.UNCHANGED);
            }
        }

        if (!itemChanges.isEmpty()) {
            changeType = oldValue == null ? ChangeType.ADD : ChangeType.UPDATE;
        }
    }

    @Override
    protected void serializeContent(XMLStreamWriter writer) throws XMLStreamException {
        COLLECTION_AUDIT_SERIALIZER.startSerialize(writer, this);
        for (ChangeNode itemChange : itemChanges) {
            itemChange.serialize(writer);
        }
        COLLECTION_AUDIT_SERIALIZER.endSerialize(writer);
    }

    protected void addItemChange(PrepareChangesContext context, Object item, ChangeType ct) {
        EntityChangeNode entityChange = cascadeChange(item, context);
        ct = ct == ChangeType.UNCHANGED ? entityChange.getChangeType() : ct;
        if (ct == ChangeType.UNCHANGED) {
            return;
        }
        itemChanges.add(new CollectionItemChange(ct, entityChange));
    }

    private EntityChangeNode cascadeChange(Object item, PrepareChangesContext context) {
        EntityChangeNode entityChange;
        if (descriptor.isCascade()) {
            entityChange = context.getChange(item);
            if (entityChange == null) {
                EntityChangeDescriptor itemDescriptor = context.getDescriptor(item);
                entityChange = itemDescriptor.newEntityChange(item);
            }
            entityChange.prepare(context);
        } else {
            entityChange = context.getDescriptor(item).newEmptyEntityChange(item);
        }


        return entityChange;
    }

    protected Set toSet(Collection collection) {
        if (collection == null) {
            return Collections.emptySet();
        }

        return new HashSet<Object>(collection);
    }

    private static Set removeDuplicates(Set set1, Set set2) {
        Set result = new HashSet<Object>();

        Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            if (set1.remove(item)) {
                iterator.remove();
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public Iterator<ChangeNode> getChildNodes() {
        return itemChanges != null ? itemChanges.iterator() : EMPTY_CHANGES;
    }

    public static class Factory implements FieldChangeFactory {
        @Override
        public final CollectionFieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            Collection newCol = (Collection) newValue;
            Collection oldCol = (Collection) oldValue;

            if (!Hibernate.isInitialized(newCol)) {
                return null;
            }

            if (oldCol == newCol && newCol instanceof PersistentCollection) {
                // must be persistent collection changed in-place
                PersistentCollection persistentCollection = (PersistentCollection) newCol;
                Serializable snapshot = persistentCollection.getStoredSnapshot();
                if (snapshot instanceof Map) {
                    Map map = (Map) snapshot;
                    oldCol = map.values();
                }  else {
                    oldCol = (Collection) snapshot;
                }
            }

            int oldSize = oldCol != null ? oldCol.size() : 0;
            int newSize = newCol != null ? newCol.size() : 0;

            // both empty
            if (oldSize == 0 && newSize == 0) {
                return null;
            }

            return newInstanceInternal(descriptor, oldCol, newCol);
        }

        protected CollectionFieldChange newInstanceInternal(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
            return new CollectionFieldChange(descriptor, oldValue, newValue);
        }
    }
}
