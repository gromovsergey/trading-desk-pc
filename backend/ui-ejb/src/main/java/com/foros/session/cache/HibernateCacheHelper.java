package com.foros.session.cache;

import com.foros.model.VersionEntityBase;
import org.hibernate.LockMode;
import org.hibernate.cache.CacheKey;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.Versioning;
import org.hibernate.event.EventSource;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Author: Boris Vanin
*/
final class HibernateCacheHelper {

    private EventSource source;
    private LockMode lockMode;
    private Object entity;
    private EntityEntry entry;
    private EntityPersister persister;

    HibernateCacheHelper(EventSource source, LockMode lockMode, Object entity) {
        this.source = source;
        this.lockMode = lockMode;
        this.entity = source.getPersistenceContext().unproxyAndReassociate( entity );
        this.entry = source.getPersistenceContext().getEntry(this.entity);
        this.persister = entry.getPersister();
    }

    boolean needCacheUpdate() {
        return persister.hasCache() && lockMode == LockMode.FORCE && (entity instanceof VersionEntityBase);
    }

    public void update() {
        // create cache key and value for update
        CacheKey cacheKey = createCacheKey();
        Object cacheEntry = createCacheEntry();

        EntityRegionAccessStrategy strategy = persister.getCacheAccessStrategy();
        // fetch old version of entity
        CacheEntry oldEntry = (CacheEntry) strategy.get(cacheKey, source.getTimestamp());
        // update
        if (oldEntry != null) {
            strategy.update(cacheKey, cacheEntry, entry.getVersion(), oldEntry.getVersion());
        }
    }

    private Object createCacheEntry() {
        Object[] state = entry.getLoadedState();
        return persister.getCacheEntryStructure().structure( new CacheEntry(
                state,
                persister,
                persister.hasUninitializedLazyProperties( entity, source.getEntityMode() ),
                Versioning.getVersion(state, persister),
                source,
                entity
        ));
    }

    private CacheKey createCacheKey() {
        return new CacheKey(
                entry.getId(),
                persister.getIdentifierType(),
                persister.getRootEntityName(),
                source.getEntityMode(),
                source.getFactory()
        );
    }

}
