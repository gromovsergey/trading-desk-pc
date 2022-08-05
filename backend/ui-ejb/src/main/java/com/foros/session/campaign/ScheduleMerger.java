package com.foros.session.campaign;

import com.foros.model.EntityBase;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Iterator;

public abstract class ScheduleMerger<T extends EntityBase> {

    protected abstract EntityManager getEm();

    public void merge(EntityBase entity, Collection<T> persisted, Collection<T> updated, String scheduleType) {
        if (entity.isChanged(scheduleType)) {

            for (Iterator<T> it = persisted.iterator(); it.hasNext();) {
                T tp = it.next();
                if (updated.contains(tp)) {
                    updated.remove(tp);
                } else {
                    getEm().remove(tp);
                    it.remove();
                }
            }

            for (Iterator<T> it = updated.iterator(); it.hasNext();) {
                T tu = it.next();
                getEm().persist(tu);
                persisted.add(tu);
            }

            entity.unregisterChange(scheduleType);
        }
    }
}
