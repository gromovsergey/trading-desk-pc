package app.programmatic.ui.common.tool.javabean;

import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.collection.IdentityHashSet;
import app.programmatic.ui.common.tool.javabean.emptyValues.EmptyValuesStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class EntityBeanUtils {

    public static final <T extends EntityBase> JavaBeanAccessor<T> getBeanAccessor(T object) {
        Class<T> javaBeanClass = (Class<T>)(object.getClass());
        Class<?> stopClass = object instanceof VersionEntityBase ? VersionEntityBase.class : EntityBase.class;
        return JavaBeanUtils.createJavaBeanAccessor(javaBeanClass, stopClass);
    }

    public static final <T extends EntityBase> EntityChangesMap mergeEntityWithPrev(T entity, T prev,
            EmptyValuesStrategy emptyValuesStrategy) {
        if (prev == null) {
            prev = (T)EntityBase.getEmpty(entity.getClass());
        }

        return mergeEntityWithPrev(entity, prev, new IdentityHashSet<>(), emptyValuesStrategy);
    }

    private static final <T extends EntityBase> EntityChangesMap mergeEntityWithPrev(T entity, T prev,
            IdentityHashSet<EntityBase> processed, EmptyValuesStrategy emptyValuesStrategy) {
        // Avoiding infinite loop
        if (!processed.add(entity) || prev == null || entity == prev) {
            return EntityChangesMap.getEmpty();
        }

        JavaBeanAccessor<T> accessor = getBeanAccessor(prev);
        ArrayList<PropertyChange> changes = new ArrayList<>(accessor.getPropertyNames().size());

        ArrayList<EntityBase> childEntities = new ArrayList<>(accessor.getPropertyNames().size());
        ArrayList<EntityBase> childPrevEntities = new ArrayList<>(accessor.getPropertyNames().size());
        accessor.walk( (String propertyName, MethodHandleProperty handle) -> {
                PropertyChangeImpl info = emptyValuesStrategy.processProperty(
                        propertyName, handle, entity, prev);

                if (info.isTransferred()) {
                    return;
                }

                if (info.isChanged()) {
                    info = transformChanges(info);
                    changes.add(info);
                }

                if (info.getNewValue() instanceof EntityBase) {
                    EntityBase<?> updatedValue = (EntityBase<?>)info.getNewValue();
                    childEntities.add(updatedValue);

                    EntityBase<?> prevValue = (EntityBase<?>)info.getPrevValue();
                    childPrevEntities.add(prevValue != null ? prevValue : EntityBase.getEmpty(updatedValue.getClass()));
                }
        });
        EntityChangesMap entityChangesMap = new EntityChangesMap();
        entityChangesMap.put(entity, changes);

        Iterator<EntityBase> prevIt = childPrevEntities.iterator();
        childEntities.stream().forEach(
            t -> entityChangesMap.merge(mergeEntityWithPrev(t, prevIt.next(), processed, emptyValuesStrategy))
        );

        return entityChangesMap;
    }

    private static PropertyChangeImpl transformChanges(PropertyChangeImpl src) {
        // Collection may be persistent and will be updated by ORM
        // In this case we must create new collection
        if (src.getPrevValue() instanceof Collection ||
                src.getNewValue() instanceof Collection) {
            Collection<?> prevCollection = src.getPrevValue() == null ? Collections.emptyList() :
                    new ArrayList<>((Collection<?>)src.getPrevValue());
            Collection<?> newCollection = src.getNewValue() == null ? Collections.emptyList() :
                    new ArrayList<>((Collection<?>)src.getNewValue());
            return new PropertyChangeImpl(src.getName(),
                                          newCollection,
                                          prevCollection,
                                          src.isTransferred());
        }

        return src;
    }
}
