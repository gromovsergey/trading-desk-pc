package com.foros.util.jpa;

import com.foros.util.CollectionUtils;
import com.foros.util.SQLUtil;
import com.foros.util.mapper.Converter;
import com.foros.util.mapper.Pair;
import org.hibernate.ejb.HibernateQuery;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

abstract class BaseQueryWrapper<R> implements QueryWrapper<R> {
    protected EntityManager em;
    protected String query;
    private Integer maxResults = null;
    private Integer firstResult = null;
    private Map<String, Object> hints = new HashMap<String, Object>();
    private Map<String, Object> params = new HashMap<String, Object>();
    private Map<String, Pair<Object, UserType>> typedParams = new HashMap<String, Pair<Object, UserType>>();
    private Map<String, Pair<Date, TemporalType>> dateParams = new HashMap<String, Pair<Date, TemporalType>>();
    private Map<String, Pair<Calendar, TemporalType>> calendarParams = new HashMap<String, Pair<Calendar, TemporalType>>();
    private Map<String, Iterable> arrayParams = new HashMap<String, Iterable>();
    private Map<String, Iterable> primitiveArrayParams = new HashMap<String, Iterable>();

    protected abstract Query createQuery(String query);

    protected abstract String createCountQuery(String query);

    public BaseQueryWrapper(EntityManager em, String query) {
        this.em = em;
        this.query = query;
    }

    @Override
    public QueryWrapper<R> setArrayParameter(String name, Iterable<?> array) {
        arrayParams.put(name, array);
        return this;
    }

    @Override
    public <T> QueryWrapper<R> setPrimitiveArrayParameter(String name, Iterable<T> array) {
        primitiveArrayParams.put(name, array);
        return this;
    }

    @Override
    public String getQueryForLog() {
        StringBuilder buf = new StringBuilder(prepareQuery(query));
        buf.append('\n');
        if (!hints.isEmpty()) {
            buf.append("Hints:\n");
            for (Map.Entry<String, Object> entry : hints.entrySet()) {
                buf.append("  ").append(entry.getKey()).append("=").append(entry.getValue()).append('\n');
            }
        }

        if (!params.isEmpty()) {
            buf.append("Params:\n");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                buf.append("  ").append(entry.getKey()).append("=").append(entry.getValue()).append('\n');
            }
        }

        if (!typedParams.isEmpty()) {
            buf.append("Typed Params:\n");
            for (Map.Entry<String, Pair<Object, UserType>> entry : typedParams.entrySet()) {
                buf.append("  ").append(entry.getKey()).append("=").append(entry.getValue().getLeftValue()).append(",").append(entry.getValue().getRightValue()).append('\n');
            }
        }

        if (!dateParams.isEmpty()) {
            buf.append("Date Params:\n");
            for (Map.Entry<String, Pair<Date, TemporalType>> entry : dateParams.entrySet()) {
                buf.append("  ").append(entry.getKey()).append("=").append(entry.getValue().getLeftValue()).append(",").append(entry.getValue().getRightValue()).append('\n');
            }
        }

        if (!calendarParams.isEmpty()) {
            buf.append("Calendar Params:\n");
            for (Map.Entry<String, Pair<Calendar, TemporalType>> entry : calendarParams.entrySet()) {
                buf.append("  ").append(entry.getKey()).append("=").append(entry.getValue().getLeftValue()).append(",").append(entry.getValue().getRightValue()).append('\n');
            }
        }

        return buf.toString();
    }

    private Query createQuery() {
        String preparedQuery = prepareQuery(query);

        Query q = createQuery(preparedQuery);

        populateQuery(q, false);

        return q;
    }

    protected void populateQuery(Query q, boolean count) {
        if (maxResults != null && !count) {
            q.setMaxResults(maxResults);
        }

        if (firstResult != null && !count) {
            q.setFirstResult(firstResult);
        }

        for (Map.Entry<String, Object> entry : hints.entrySet()) {
            q.setHint(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }

        if (!typedParams.isEmpty()) {
            org.hibernate.Query query = ((HibernateQuery) q).getHibernateQuery();
            for (Map.Entry<String, Pair<Object, UserType>> entry : typedParams.entrySet()) {
                Type type = new CustomType(entry.getValue().getRightValue(), null);
                query.setParameter(entry.getKey(), entry.getValue().getLeftValue(), type);
            }
        }

        for (Map.Entry<String, Pair<Date, TemporalType>> entry : dateParams.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue().getLeftValue(), entry.getValue().getRightValue());
        }

        for (Map.Entry<String, Pair<Calendar, TemporalType>> entry : calendarParams.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue().getLeftValue(), entry.getValue().getRightValue());
        }
    }

    protected String prepareQuery(String query) {
        String result = query;

        for (Map.Entry<String, Iterable> entry : arrayParams.entrySet()) {
            String name = entry.getKey();
            String replace = addParameters(name, entry.getValue());
            result = result.replaceAll("(:" + name + ")[\\W$]?", replace);
        }

        for (Map.Entry<String, Iterable> entry : primitiveArrayParams.entrySet()) {
            String replace = CollectionUtils.toString(new Converter<Object, String>() {

                @Override
                public String item(Object value) {
                    return asString(value);
                }

                private String asString(Object value) {
                    if (value instanceof Number) {
                        return value.toString();
                    } else if (value instanceof Character) {
                        return "\'" + value + "\'";
                    } else if(value instanceof Class) {
                        return ((Class) value).getSimpleName();
                    } else {
                        return "\"" + value.toString() + "\"";
                    }
                }
            }, false, entry.getValue());
            result = result.replaceAll("(:" + entry.getKey() + ")([\\W$])?", "(" + replace + ")$2");
        }

        return result;
    }

    private String addParameters(final String name, Iterable value) {
        return "(" + CollectionUtils.toString(new Converter<Object, String>() {
            private int counter = 0;
            @Override
            public String item(Object value) {
                String itemName = name + counter++;
                setParameter(itemName, value);
                return ":" + itemName;
            }
        }, false, value) + ") ";
    }

    @Override
    public List<R> getResultList() {
        return createQuery().getResultList();
    }

    @Override
    public R getSingleResult() {
        return (R) createQuery().getSingleResult();
    }

    @Override
    public DetachedList<R> getDetachedList() {
        return new DetachedList<R>(getResultList(), executeCount());
    }

    @Override
    public DetachedList<R> getDetachedList(int firstResult, int maxResult) {
        setFirstResult(firstResult);
        setMaxResults(maxResult);
        return new DetachedList<R>(getResultList(), executeCount());
    }

    @Override
    public int executeUpdate() {
        return createQuery().executeUpdate();
    }

    @Override
    public QueryWrapper<R> setMaxResults(int i) {
        maxResults = i;
        return this;
    }

    @Override
    public QueryWrapper<R> setFirstResult(int i) {
        firstResult = i;
        return this;
    }

    @Override
    public QueryWrapper<R> setHint(String s, Object o) {
        hints.put(s, o);
        return this;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Object o) {
        if (o instanceof Iterable) {
            setArrayParameter(s, (Iterable<?>) o);
        } else {
            params.put(s, o);
        }

        return this;
    }

    @Override
    public QueryWrapper<R> setLikeParameter(String param, String value) {
        if (value == null) {
            value = "";
        }
        params.put(param, "%" + SQLUtil.getEscapedString(value, '\\') + "%");

        return this;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Date date, TemporalType temporalType) {
        dateParams.put(s, new Pair<Date, TemporalType>(date, temporalType));
        return this;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Calendar calendar, TemporalType temporalType) {
        calendarParams.put(s, new Pair<Calendar, TemporalType>(calendar, temporalType));
        return this;
    }

    @Override
    public QueryWrapper<R> setParameter(String s, Object o, UserType type) {
        typedParams.put(s, new Pair<Object, UserType>(o, type));
        return this;
    }

    /**
     * Returns the number of rows in a query regardless of 'MaxResult' value.
     * It is useful when we need to restrict the size of returning list by 'MaxResult' parameter, but interested in
     * amount of overall selection result.
     *
     * @return returns the number of rows in a query, regardless of  'MaxResult' value.
     */
    @Override
    public int executeCount() {
        String preparedQuery = prepareQuery(createCountQuery(query));

        Query q = createQuery(preparedQuery);

        populateQuery(q, true);

        return ((Number)q.getSingleResult()).intValue();
    }

    @Override
    public QueryWrapper<R> oneIf(boolean condition) {
        if (condition) {
            return this;
        } else {
            return new SkipNextQueryWrapper<R>(this);
        }
    }
}
