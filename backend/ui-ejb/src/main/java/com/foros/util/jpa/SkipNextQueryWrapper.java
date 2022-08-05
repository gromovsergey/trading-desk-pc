package com.foros.util.jpa;

import org.hibernate.usertype.UserType;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.persistence.TemporalType;

class SkipNextQueryWrapper<R> implements QueryWrapper<R> {

    private QueryWrapper<R> target;

    public SkipNextQueryWrapper(QueryWrapper<R> target) {
        this.target = target;
    }

    @Override
    public QueryWrapper<R> setArrayParameter(String name, Iterable<?> array) {
        return target;
    }

    @Override
    public <T> QueryWrapper<R> setPrimitiveArrayParameter(String name, Iterable<T> array) {
        return target;
    }

    @Override
    public List<R> getResultList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public R getSingleResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachedList<R> getDetachedList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetachedList<R> getDetachedList(int page, int pageSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryWrapper<R> setMaxResults(int i) {
        return target;
    }

    @Override
    public QueryWrapper<R> setFirstResult(int i) {
        return target;
    }

    @Override
    public QueryWrapper<R> setHint(String s, Object o) {
        return target;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Object o) {
        return target;
    }

    @Override
    public QueryWrapper<R> setLikeParameter(String param, String value) {
        return target;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Date date, TemporalType temporalType) {
        return target;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Calendar calendar, TemporalType temporalType) {
        return target;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Object o, UserType type) {
        return target;
    }

    @Override
    public int executeCount() {
       throw new UnsupportedOperationException();
    }

    @Override
    public QueryWrapper<R> oneIf(boolean condition) {
        return target;
    }

    @Override
    public String getQueryForLog() {
        throw new UnsupportedOperationException();
    }
}
