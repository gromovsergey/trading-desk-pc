package app.programmatic.ui.common.aspect.prePersistProcessor.impl;

import app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType;
import app.programmatic.ui.common.aspect.prePersistProcessor.PrePersistProcessorContext;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.EntityChangesGetter;
import app.programmatic.ui.common.tool.javabean.EntityChangesMap;

import java.util.Set;

interface PrePersistProcessorContextInternal extends PrePersistProcessorContext {

    void mergeChanges(EntityChangesMap entityChangesMap);
    EntityChangesGetter getChanges();

    void addEntityOperation(EntityBase<?> entity, ServiceOperationType operationType);
    Set<EntityOperation> getEntityOperations();

    void clear();

    class EntityOperation {
        private EntityBase<?> entity;
        private ServiceOperationType operationType;

        public EntityOperation(EntityBase<?> entity, ServiceOperationType operationType) {
            this.entity = entity;
            this.operationType = operationType;
        }

        public EntityBase<?> getEntity() {
            return entity;
        }

        public ServiceOperationType getOperationType() {
            return operationType;
        }
    }
}
