package com.foros.persistence.hibernate;

import com.foros.audit.changes.ChangeRecord;
import com.foros.audit.changes.DatabaseChangesService;
import com.foros.cache.generic.Cache;
import com.foros.cache.generic.CacheProviderService;
import com.foros.cache.generic.EntityIdTag;
import com.foros.model.creative.Creative;
import com.foros.session.ServiceLocator;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PostgresChangesTrackerInterceptor {
    private static final Logger LOGGER = Logger.getLogger(PostgresChangesTrackerInterceptor.class.getName());

    private Session session;
    private EvictCacheHibernateInterceptor evictCacheInterceptor;

    public PostgresChangesTrackerInterceptor(EvictCacheHibernateInterceptor evictCacheInterceptor) {
        this.evictCacheInterceptor = evictCacheInterceptor;
    }

    public void scheduleEvict(Session session) {
        this.session = session;
    }

    public void evict() {
        if (session != null) {
            DatabaseChangesService service = ServiceLocator.getInstance().lookup(DatabaseChangesService.class);
            Collection<ChangeRecord> records = service.readChanges();

            evictHibernateCache(records);
            evictReportsCache(records);
        }

        session = null;
    }

    private void evictHibernateCache(Collection<ChangeRecord> records) {
        try {
            SessionFactory factory = session.getSessionFactory();

            for (ChangeRecord record : records) {
                evictEntityWithCollections(factory, record);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void evictEntityWithCollections(SessionFactory factory, ChangeRecord record) {
        factory.evictEntity(record.getClassName(), record.getPrimaryKey());
        if (Creative.class.getName().equals(record.getClassName())) {
            factory.evictCollection(Creative.class.getName() + ".categories", record.getPrimaryKey());
        }
    }

    /** i.e. Memcached */
    private void evictReportsCache(Collection<ChangeRecord> records) {
        if (!evictCacheInterceptor.isInitialized()) {
            Cache cache = ServiceLocator.getInstance().lookup(CacheProviderService.class).getCache();
            evictCacheInterceptor.initialize(session, cache);
        }

        try {
            for (ChangeRecord record : records) {
                Class<?> entityClass = Class.forName(record.getClassName());
                if (EvictableAnnotationHelper.isEvictable(entityClass)) {
                    EntityIdTag tag = EntityIdTag.create(session, entityClass, record.getPrimaryKey());
                    evictCacheInterceptor.touchTag(tag);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
