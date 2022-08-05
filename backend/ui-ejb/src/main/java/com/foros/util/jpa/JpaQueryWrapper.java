package com.foros.util.jpa;

import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class JpaQueryWrapper<R> extends BaseQueryWrapper<R> {
    private static final Pattern SELECT_FROM_PATTERN = Pattern.compile("^(?i)\\s*select .+ from");
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("(?i)\\s*order\\s*by.+$");

    public JpaQueryWrapper(EntityManager em, String query) {
        super(em, query);
    }

    protected Query createQuery(String query) {
        return em.createQuery(query);
    }

    protected String createCountQuery(String query) {
        String result = SELECT_FROM_PATTERN.matcher(query).replaceAll("select count(*) from");
        return ORDER_BY_PATTERN.matcher(result).replaceAll("");
    }
}
