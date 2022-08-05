package com.foros.test.factory;

import com.foros.model.Identifiable;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.util.RandomUtil;
import com.foros.util.StringUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public abstract class TestFactory<T> {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager entityManager;

    @Autowired
    protected LoggingJdbcTemplate jdbcTemplate;

    public static final String OUI_TEST_ENTITY_NAME_PREFIX = "UTE-";

    public abstract T create();

    public abstract void persist(T entity);

    public abstract void update(T entity);

    public abstract T createPersistent();

    public static String getTestEntityRandomName() {
        return OUI_TEST_ENTITY_NAME_PREFIX + UUID.randomUUID().toString();
    }

    public T findAny(Class<T> clazz, QueryParam... params) {
        if (params == null || params.length == 0) {
            throw new IllegalArgumentException("Specify query's params to make sure a test won't use invalid entities (with Id = -1 etc)");
        }

        String name = clazz.getAnnotation(Entity.class).name();
        if (StringUtil.isPropertyEmpty(name)) {
            name = clazz.getSimpleName();
        }

        Query query = createQuery("select count(n) from " + name + " n", params);
        Long countResult = (Long) query.getSingleResult();

        if (countResult == null || countResult == 0L) {
            throw new IllegalStateException("Can't find any persisted instance of a class " + clazz.getSimpleName());
        }

        Long randomIndex = RandomUtil.getRandomLong(countResult);

        query = createQuery("select n from " + name + " n", params);
        query.setFirstResult(randomIndex.intValue()).setMaxResults(1);

        @SuppressWarnings("unchecked")
        T result = (T) query.getSingleResult();

        return (result);
    }

    private Query createQuery(String sql, QueryParam... params) {
        // build where clause
        if (params[0] != null) {
            StringBuilder where = new StringBuilder();
            for (QueryParam param : params) {
                if (where.length() > 0) {
                    where.append(" and ");
                }
                where.append("n.").append(param.getName());
                where.append(param.isEqual() ? " = " : " <> ");
                where.append(":").append(parameterName(param));
            }
            sql += " where " + where.toString();
        }

        // prepare query
        Query query = entityManager.createQuery(sql);

        // set parameters value
        if (params[0] != null) {
            for (QueryParam param : params) {
                query.setParameter(parameterName(param), param.getValue());
            }
        }

        return query;
    }

    private String parameterName(QueryParam param) {
        return param.getName().replace('.', '_');
    }

    public Integer queryForInt(String sql) {
        Query query = entityManager.createNativeQuery(sql);

        BigDecimal result = (BigDecimal) query.getSingleResult();

        return result.intValue();
    }

    public Long queryForSequence(String sql) {
        List results = entityManager.createNativeQuery(sql).getResultList();

        Number result = (Number) results.iterator().next();

        return result.longValue();
    }

    public Long queryForLong(String sql) {
        Query query = entityManager.createNativeQuery(sql);

        Number result = (Number) query.getSingleResult();

        return result.longValue();
    }

    public String queryForString(String sql) {
        Query query = entityManager.createNativeQuery(sql);

        String result = (String) query.getSingleResult();

        return (result);
    }

    public void clearContext() {
        entityManager.flush();
        entityManager.clear();
    }

    public void loadLazy(final Object o) {
        Hibernate.initialize(o);
        ReflectionUtils.doWithFields(o.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                Hibernate.initialize(field.get(o));
            }
        });
    }

    public void deleteEntity(T entity) {
        entityManager.remove(entity);
        entityManager.flush();
    }

    public T refresh(T entity) {
        try {
            entityManager.refresh(entity);
        } catch (IllegalArgumentException e) {
            // entity not managed
            //noinspection unchecked
            entity = (T) entityManager.find(entity.getClass(), ((Identifiable) entity).getId());
            entityManager.refresh(entity);
        }
        return entity;
    }
}
