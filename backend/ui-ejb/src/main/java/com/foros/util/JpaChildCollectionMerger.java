package com.foros.util;

import com.foros.model.EntityBase;

import java.util.Collection;

public abstract class JpaChildCollectionMerger<E extends EntityBase, T extends EntityBase> extends JpaCollectionMerger<T> {

    private E parent;
    private String propertyName;

    public JpaChildCollectionMerger(E parent, String propertyName, Collection<T> persisted, Collection<T> updated) {
        super(persisted, updated);
        this.parent = parent;
        this.propertyName = propertyName;
    }

    @Override
    public void merge() {
        //UnregisterChange must be the first
        parent.unregisterChange(propertyName);
        super.merge();
    }

    public final E getParent() {
        return parent;
    }

}
