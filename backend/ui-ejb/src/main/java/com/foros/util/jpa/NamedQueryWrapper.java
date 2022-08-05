package com.foros.util.jpa;

import javax.naming.OperationNotSupportedException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class NamedQueryWrapper<R> extends BaseQueryWrapper<R> {

    public NamedQueryWrapper(EntityManager em, String query) {
        super(em, query);
    }

    protected Query createQuery(String query) {
        return em.createNamedQuery(query);
    }

    @Override
    protected String createCountQuery(String query) {
        throw new UnsupportedOperationException("count function for Named Queries is not implemented");
    }
}