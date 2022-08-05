package com.foros.util.jpa;

import org.hibernate.usertype.UserType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.TemporalType;

public interface QueryWrapper<R> {

    QueryWrapper<R> setArrayParameter(String name, Iterable<?> array);


    <T> QueryWrapper<R> setPrimitiveArrayParameter(String name, Iterable<T> array);

    List<R> getResultList();

    R getSingleResult();

    DetachedList<R> getDetachedList();

    DetachedList<R> getDetachedList(int page, int pageSize);

    int executeUpdate();

    QueryWrapper<R> setMaxResults(int i);

    QueryWrapper<R> setFirstResult(int i);

    QueryWrapper<R> setHint(String s, Object o);

    QueryWrapper<R> setParameter(String s, Object o);

    QueryWrapper<R> setLikeParameter(String param, String value);

    QueryWrapper<R> setParameter(String s, Date date, TemporalType temporalType);

    QueryWrapper<R> setParameter(String s, Calendar calendar, TemporalType temporalType);

    QueryWrapper<R> setParameter(String s, Object o, UserType type);

    int executeCount();

    // TODO: beginIf, endIf
    QueryWrapper<R> oneIf(boolean condition);

    String getQueryForLog();
}
