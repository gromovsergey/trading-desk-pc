package com.foros.util;

import java.util.Collection;

import javax.persistence.EntityManager;

public abstract class JpaCollectionMerger<T> extends CollectionMerger<T> {

    protected abstract EntityManager getEM();

    public JpaCollectionMerger(Collection<T> persisted, Collection<T> updated) {
        super(persisted, updated);
    }

    @Override
    protected boolean add(T updated) {
        getEM().persist(updated);
        return true;
    }

    @Override
    protected void update(T persistent, T updated) {
        getEM().merge(updated);
    }

    @Override
    protected boolean delete(T persistent) {
        getEM().remove(persistent);
        return true;
    }

}
