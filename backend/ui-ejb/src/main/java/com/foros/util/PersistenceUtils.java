package com.foros.util;

import com.foros.model.Identifiable;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.util.bean.Filter;
import com.foros.util.changes.ChangesSupportCollection;
import com.foros.util.command.HibernateWork;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public final class PersistenceUtils {

    public static class EntityInfo {

        private Long id;

        private Class<?> type;

        public EntityInfo(Long id, Class<?> type) {
            this.id = id;
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public Class<?> getType() {
            return type;
        }

    }

    /**
     * Unproxy object if this needed. Never unproxy object if it not initialized by hibernate.
     *
     * @param object object for unproxy
     * @return unproxed object or the same object if unproxy do not needed or impossible
     */
    public static Object unproxyIfInitialized(Object object) {
        return isInitialized(object) ? unproxy(object) : object;
    }

    private static Object unproxy(Object object) {
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) object;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            return li.getImplementation();
        } else {
            return object;
        }
    }

    public static EntityInfo getClassInfo(Object object) {
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) object;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            Long id = li.getIdentifier() instanceof Long ? (Long) li.getIdentifier() : null;
            return new EntityInfo(id, li.getPersistentClass());
        } else {
            Long id = object instanceof Identifiable ? ((Identifiable) object).getId() : null;
            return new EntityInfo(id, object.getClass());
        }
    }

    public static Object getIdentifier(Session session, Object o) {
        return getEntityPersister(session, o).getIdentifier(o, (SessionImplementor) session);
    }

    private static EntityPersister getEntityPersister(Session session, Object entity) {
        return ((SessionImplementor) session).getEntityPersister(null, entity);
    }

    public static boolean isInitialized(Object object) {
        if (object instanceof ChangesSupportCollection) {
            object = ((ChangesSupportCollection) object).getOriginalCollection();
        }
        return Hibernate.isInitialized(object);
    }

    public static boolean initialize(Object object) {
        if (object instanceof ChangesSupportCollection) {
            object = ((ChangesSupportCollection) object).getOriginalCollection();
        }
        if (!isInitialized(object)) {
            Hibernate.initialize(object);
            return true;
        }
        return false;
    }

    public static boolean initializeCollection(Collection collection) {
        return collection.size() >= 0;
    }

    public static Session getHibernateSession(EntityManager em) {
        HibernateEntityManager hem = (HibernateEntityManager) (em instanceof HibernateEntityManager ? em : em.getDelegate());
        return hem.getSession();
    }

    public static HibernateInterceptor getInterceptor(Session session) {
        return (HibernateInterceptor) ((SessionImpl) session).getInterceptor();
    }

    public static HibernateInterceptor getInterceptor(EntityManager manager) {
        return getInterceptor(getHibernateSession(manager));
    }

    public static void scheduleEviction(EntityManager em) {
        scheduleEviction(getHibernateSession(em));
    }

    public static void scheduleEviction(Session session) {
        SessionImpl sessionImpl = (SessionImpl) session;
        HibernateInterceptor hi = (HibernateInterceptor) sessionImpl.getInterceptor();
        hi.getPostgresChangesTrackerInterceptor().scheduleEvict(session);
    }

    public static boolean isSameClass(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        }

        if (o1.getClass() == o2.getClass()) {
            return true;
        }

        return Hibernate.getClass(o1) == Hibernate.getClass(o2);
    }

    public static <T> T execute(Session session, HibernateWork<T> work) {
        return work.execute(session);
    }

    // TODO use form Hibernate because there is bug in version 3.6.10 up 4.0
    public static void performHibernateLock(EntityManager em, Object obj) {
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        performHibernateLock(hem.getSession(), obj);
    }

    public static void performHibernateLock(Session session, Object obj) {
        try {
            session
                    .buildLockRequest(LockOptions.UPGRADE)
                    .setLockMode(LockMode.FORCE)
                    .lock(obj);
        } catch (StaleObjectStateException ex) {
            throw new OptimisticLockException(ex);
        }
    }

    public static String getRootEntityName(EntityManager em, Class<?> type) {
        return getRootEntityName(getHibernateSession(em), type);
    }

    public static String getRootEntityName(Session session, Class<?> type) {
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) session.getSessionFactory();

        ClassMetadata classMetadata = sessionFactory.getClassMetadata(type);

        if (classMetadata == null) {
            throw new RuntimeException("Can't find persistent metadata for " + type);
        }

        EntityPersister persister = sessionFactory.getEntityPersister(classMetadata.getEntityName());

        return fetchRootName(type, persister);
    }

    public static String getRootEntityName(Session session, Object entity) {
        SessionImplementor sessionImplementor = (SessionImplementor) session;

        return fetchRootName(entity.getClass(),
                sessionImplementor.getEntityPersister(null, entity));
    }

    private static String fetchRootName(Class<?> type, EntityPersister persister) {
        if (persister == null) {
            throw new RuntimeException("Can't find persister metadata for " + type);
        }

        return persister.getRootEntityName();
    }

    public static void flushAndClear(EntityManager em, Filter<Integer> filter) {
        if (filter.accept(getHibernateSession(em).getStatistics().getEntityCount())) {
            em.flush();
            HibernateInterceptor hi = getInterceptor(em);
            hi.processBeforeTransactionCompletion();
            em.clear();
        }

    }

    public static void flushAndClear(EntityManager em) {
        flushAndClear(em, new Filter<Integer>() {
            @Override
            public boolean accept(Integer element) {
                return true;
            }
        });
    }
}
