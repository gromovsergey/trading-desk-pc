package com.foros.util;

import com.foros.model.EntityBase;
import com.foros.model.Identifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionMerger<T> {
    protected Collection<T> persisted;
    protected Collection<T> updated;

    public CollectionMerger(Collection<T> persisted, Collection<T> updated) {
        this.persisted = persisted;
        this.updated = updated;
    }

    public void merge() {
        Map<Object, T> persistedIds = toIdSet(persisted);
        Map<Object, T> updatedIds = toIdSet(updated);

        int index = 0;
        for (Iterator<T> it = persisted.iterator(); it.hasNext();) {
            T tp = it.next();
            T tu = updatedIds.get(getId(tp, index));
            if (tu == null) {
                // delete
                if (delete(tp)) {
                    it.remove();
                }
            } else {
                // update
                update(tp, tu);
            }
            index++;
        }

        index = 0;
        for (T tu : updated) {
            T tp = persistedIds.get(getId(tu, index));
            if (tp == null) {
                // add
                if (add(tu)) {
                    persisted.add(tu);
                }
            }
            index++;
        }
    }

    protected Object getId(T t, int index) {
        return ((Identifiable)t).getId();
    }

    protected boolean add(T updated) {
        return true;
    }

    protected void update(T persistent, T updated) {
        EntityUtils.copy((EntityBase)persistent, (EntityBase)updated);
    }

    protected boolean delete(T updated) {
        return true;
    }

    protected Map<Object, T> toIdSet(Collection<T> collection) {
        Map<Object, T> map = new HashMap<Object, T>(collection.size());
        int index = 0;
        for (T t : collection) {
            if (getId(t, index) != null) {
                map.put(getId(t, index), t);
            }
            index++;
        }
        return map;
    }
}
