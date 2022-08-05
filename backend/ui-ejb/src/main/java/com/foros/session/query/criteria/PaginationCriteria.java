package com.foros.session.query.criteria;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.transform.ResultTransformer;

public interface PaginationCriteria {

    boolean isUseIdCriteria();

    PaginationCriteria add(Criterion criterion);

    PaginationCriteria addOrder(Order order);

    PaginationCriteria createAlias(String associationPath, String alias) throws HibernateException;

    PaginationCriteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException;

    PaginationCriteria setProjection(Projection projection);

    PaginationCriteria setResultTransformer(ResultTransformer resultTransformer);

    PaginationCriteria createAlias(String associationPath, String alias, int joinType) throws HibernateException;

    DetachedCriteria getDataCriteria();

    DetachedCriteria getIdCriteria();

    DetachedCriteria getCountCriteria();

    boolean isUseCountCriteria();
}
