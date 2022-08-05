package com.foros.util.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JpaCountQueryWrapper<R> extends BaseQueryWrapper<R> {
    private String countQuery;

    public JpaCountQueryWrapper(EntityManager em, String query, String countQuery) {
        super(em, query);

        this.countQuery = countQuery;
    }

    protected Query createQuery(String query) {
        return em.createQuery(query);
    }

    protected String createCountQuery(String query) {
        return countQuery;
    }
}
