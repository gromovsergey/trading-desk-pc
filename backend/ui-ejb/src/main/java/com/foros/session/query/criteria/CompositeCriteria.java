package com.foros.session.query.criteria;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;

public class CompositeCriteria implements PaginationCriteria {

    private final DetachedCriteria dataCriteria;

    private final DetachedCriteria idCriteria;

    private final DetachedCriteria countCriteria;

    private CompositeCriteria(Builder builder) {
        this.dataCriteria = builder.dataCriteria;
        this.idCriteria = builder.idCriteria;
        if (idCriteria != null) {
            this.idCriteria.setProjection(Projections.id());
        }
        this.countCriteria = builder.countCriteria;
    }

    @Override
    public boolean isUseIdCriteria() {
        return idCriteria != null;
    }

    @Override
    public boolean isUseCountCriteria() {
        return countCriteria != null;
    }

    @Override
    public PaginationCriteria add(Criterion criterion) {
        dataCriteria.add(criterion);
        if (idCriteria != null) {
            idCriteria.add(criterion);
        }
        if (countCriteria != null) {
            countCriteria.add(criterion);
        }
        return this;
    }

    @Override
    public PaginationCriteria addOrder(Order order) {
        dataCriteria.addOrder(order);
        if (idCriteria != null) {
            idCriteria.addOrder(order);
        }
        return this;
    }

    @Override
    public PaginationCriteria createAlias(String associationPath, String alias) throws HibernateException {
        dataCriteria.createAlias(associationPath, alias);
        if (idCriteria != null) {
            idCriteria.createAlias(associationPath, alias);
        }
        if (countCriteria != null) {
            countCriteria.createAlias(associationPath, alias);
        }
        return this;
    }

    @Override
    public PaginationCriteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException {
        dataCriteria.setFetchMode(associationPath, mode);
        if (idCriteria != null) {
            idCriteria.setFetchMode(associationPath, mode);
        }
        if (countCriteria != null) {
            countCriteria.setFetchMode(associationPath, mode);
        }
        return this;
    }

    @Override
    public PaginationCriteria setProjection(Projection projection) {
        dataCriteria.setProjection(projection);
        if (idCriteria != null) {
            idCriteria.setProjection(projection);
        }
        if (countCriteria != null) {
            countCriteria.setProjection(projection);
        }
        return this;
    }

    @Override
    public PaginationCriteria setResultTransformer(ResultTransformer resultTransformer) {
        dataCriteria.setResultTransformer(resultTransformer);
        if (idCriteria != null) {
            idCriteria.setResultTransformer(resultTransformer);
        }
        if (countCriteria != null) {
            countCriteria.setResultTransformer(resultTransformer);
        }
        return this;
    }

    @Override
    public PaginationCriteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
        dataCriteria.createAlias(associationPath, alias, joinType);
        if (idCriteria != null) {
            idCriteria.createAlias(associationPath, alias, joinType);
        }
        if (countCriteria != null) {
            countCriteria.createAlias(associationPath, alias, joinType);
        }
        return this;
    }

    @Override
    public DetachedCriteria getDataCriteria() {
        return dataCriteria;
    }

    @Override
    public DetachedCriteria getIdCriteria() {
        return idCriteria;
    }

    @Override
    public DetachedCriteria getCountCriteria() {
        return countCriteria;
    }

    public static class Builder {
        private DetachedCriteria dataCriteria;
        private DetachedCriteria idCriteria;
        private DetachedCriteria countCriteria;

        public Builder dataCriteria(DetachedCriteria dataCriteria) {
            this.dataCriteria = dataCriteria;
            return this;
        }

        public Builder idCriteria(DetachedCriteria idCriteria) {
            this.idCriteria = idCriteria;
            return this;
        }

        public Builder countCriteria(DetachedCriteria countCriteria) {
            this.countCriteria = countCriteria;
            return this;
        }

        public CompositeCriteria build() {
            return new CompositeCriteria(this);
        }
    }
}
