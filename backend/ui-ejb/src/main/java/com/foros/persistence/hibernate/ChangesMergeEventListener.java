package com.foros.persistence.hibernate;

import com.foros.model.EntityBase;
import com.foros.model.Flags;
import com.foros.util.StringUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.def.DefaultMergeEventListener;
import org.hibernate.intercept.LazyPropertyInitializer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.property.BackrefPropertyAccessor;
import org.hibernate.type.Type;

public class ChangesMergeEventListener extends DefaultMergeEventListener {

    @Override
    protected void copyValues(EntityPersister persister, Object entity, Object target, SessionImplementor source, Map copyCache) {
        if (!(entity instanceof EntityBase)) {
            super.copyValues(persister, entity, target, source, copyCache);
            return;
        }

        EntityBase origin = (EntityBase) entity;
        EntityBase targetEntity = (EntityBase) target;

        String[] propertyNames = persister.getPropertyNames();

        Object[] originalValues = persister.getPropertyValues(entity, source.getEntityMode());
        Object[] targetValues = persister.getPropertyValues(target, source.getEntityMode());

        Type[] types = persister.getPropertyTypes();

        final Object[] copiedValues =
                replace(originalValues, targetValues, types, source, origin, targetEntity, propertyNames, copyCache);

        persister.setPropertyValues(target, copiedValues, source.getEntityMode());
    }

    private static Object[] replace(
            Object[] original, Object[] target,
            Type[] types, SessionImplementor session,
            EntityBase originalEntity, EntityBase targetEntity,
            String[] propertyNames, Map copyCache) {

        Object[] copied = new Object[original.length];

        for (int i = 0; i < types.length; i++) {

            Object originalValue = original[i];
            Object targetValue = target[i];

            String propertyName = propertyNames[i];

            boolean isUnfetched = originalValue == LazyPropertyInitializer.UNFETCHED_PROPERTY;
            boolean isUnknown = originalValue == BackrefPropertyAccessor.UNKNOWN;

            boolean changed = originalEntity.isChanged(propertyName);

            if (isUnfetched || isUnknown || !changed) {
                copied[i] = targetValue;
            } else {
                if (isCopyNeeded(originalValue, targetValue)) {
                    copied[i] = types[i].replace(originalValue, targetValue, session, targetEntity, copyCache);
                    targetEntity.registerChange(propertyName);
                } else {
                    copied[i] = targetValue;
                }
            }
        }

        return copied;
    }

    private static boolean isCopyNeeded(Object originalValue, Object targetValue) {
        if (originalValue instanceof Collection) {
            return true;
        } else if (originalValue instanceof Flags) {
            Flags originalFlags = (Flags) originalValue;
            Flags targetFlags = (Flags) targetValue;
            return originalFlags.isChanged() && !targetFlags.set(originalFlags).equals(targetFlags);
        } else if (originalValue instanceof BigDecimal) {
            return targetValue == null || ((BigDecimal) originalValue).compareTo((BigDecimal) targetValue) != 0;
        } else if (originalValue instanceof String && targetValue == null
                && StringUtil.isPropertyEmpty((String) originalValue)) {
            return false;
        }  else {
            return !ObjectUtils.equals(originalValue, targetValue);
        }
    }

}
