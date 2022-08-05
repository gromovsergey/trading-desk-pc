package com.foros.util.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class NativeQueryWrapper<R> extends BaseQueryWrapper<R> {

    private String countSubquery = "*";

    public NativeQueryWrapper(EntityManager em, String query) {
        super(em, query);
    }

    public NativeQueryWrapper(EntityManager em, String query, String countSubquery) {
        this(em, query);
        this.countSubquery = countSubquery;
    }

    @Override
    protected Query createQuery(String query) {
        return em.createNativeQuery(query);
    }

    @Override
    protected String createCountQuery(String query) {
        return "select count(" + countSubquery + ") from ( " + query + " ) as count_query ";
    }
}
