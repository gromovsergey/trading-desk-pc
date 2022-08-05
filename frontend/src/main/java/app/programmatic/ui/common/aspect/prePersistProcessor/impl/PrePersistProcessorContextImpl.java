package app.programmatic.ui.common.aspect.prePersistProcessor.impl;

import app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.emptyValues.DefaultEmptyValuesStrategy;
import app.programmatic.ui.common.tool.javabean.emptyValues.EmptyValuesStrategy;
import app.programmatic.ui.common.tool.javabean.EntityChangesGetter;
import app.programmatic.ui.common.tool.javabean.EntityChangesMap;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PrePersistProcessorContextImpl implements PrePersistProcessorContextInternal {
    private static final ThreadLocal<EmptyValuesStrategy> emptyValuesStrategy =
            ThreadLocal.withInitial(DefaultEmptyValuesStrategy::getInstance);
    private static final ThreadLocal<EntityChangesMap> entityChangesMap =
            ThreadLocal.withInitial(() -> new EntityChangesMap());
    private static final ThreadLocal<Set<EntityOperation>> entityOperationsSet =
            ThreadLocal.withInitial(() -> new HashSet<>());

    @Override
    public EmptyValuesStrategy getEmptyValuesStrategy() {
        return emptyValuesStrategy.get();
    }

    @Override
    public void setEmptyValuesStrategy(EmptyValuesStrategy emptyValuesStrategy) {
        PrePersistProcessorContextImpl.emptyValuesStrategy.set(emptyValuesStrategy);
    }

    @Override
    public void mergeChanges(EntityChangesMap entityChangesMap) {
        this.entityChangesMap.get().merge(entityChangesMap);
    }

    @Override
    public EntityChangesGetter getChanges() {
        return entityChangesMap.get();
    }

    @Override
    public void addEntityOperation(EntityBase<?> entity, ServiceOperationType operationType) {
        entityOperationsSet.get().add(new EntityOperation(entity, operationType));
    }

    @Override
    public Set<EntityOperation> getEntityOperations() {
        return entityOperationsSet.get();
    }

    @Override
    public void clear() {
        emptyValuesStrategy.remove();
        entityChangesMap.remove();
        entityOperationsSet.remove();
    }
}
