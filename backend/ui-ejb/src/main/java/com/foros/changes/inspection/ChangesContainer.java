package com.foros.changes.inspection;

import com.foros.changes.inspection.changeNode.EntityChangeNode;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ChangesContainer {
    private static final Logger logger = Logger.getLogger(ChangesContainer.class.getName());

    private ChangeDescriptorRegistry registry;

    private Map<Object, EntityChangeNode> changes = newChanges();

    private IdentityHashMap<Object, EntityChangeNode> newChanges() {
        return new IdentityHashMap<Object, EntityChangeNode>();
    }

    public ChangesContainer(ChangeDescriptorRegistry registry) {
        this.registry = registry;
    }

    public void addChangeRecord(Object object, ChangeType changeType, Object[] newValues, Object[] oldValues) {
        EntityChangeDescriptor descriptor = registry.getDescriptor(object);
        if (descriptor == null) {
            return;
        }

        EntityChangeNode entityChange = descriptor.newEntityChange(object, changeType, oldValues, newValues);

        EntityChangeNode existingRecord = changes.get(object);
        if (existingRecord != null) {
            existingRecord.merge(entityChange);
        } else {
            changes.put(object, entityChange);
        }
    }

    public EntityChangeNode fetchChangesTree(Object root) {
        EntityChangeNode entityChange = changes.get(root);

        if (entityChange == null) {
            EntityChangeDescriptor descriptor = registry.getDescriptor(root);
            entityChange = descriptor.newEntityChange(root);
        }

        PrepareChangesContext changesContext = new PrepareChangesContext(changes, registry);
        entityChange.prepare(changesContext);

        logger.info("fetchChangesTree=" + entityChange.toString());

        return entityChange.getChangeType() == ChangeType.UNCHANGED ? null : entityChange;
    }

    public EntityChangeNode getChange(Object o) {
        return changes.get(o);
    }

    public void clear() {
        changes = newChanges();
    }
}
