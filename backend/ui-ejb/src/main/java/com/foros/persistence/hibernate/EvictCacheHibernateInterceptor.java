package com.foros.persistence.hibernate;

import com.foros.cache.generic.Cache;
import com.foros.cache.generic.EntityIdTag;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.Session;

public class EvictCacheHibernateInterceptor {
    private boolean initialized = false;

    private Set<Object> tags = new HashSet<>();

    private Session session;
    private Cache cache;

    public void initialize(Session session, Cache cache) {
        if (session == null || cache == null) {
            throw new NullPointerException("Hibernate session and cache must be not null");
        }

        this.session = session;
        this.cache = cache;
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void touchTagByEntity(Object entity) {
        if (initialized && EvictableAnnotationHelper.isEvictable(entity.getClass())) {
            touchTag(EntityIdTag.create(session, entity));
        }
    }

    public void touchTag(Object tag) {
        if (initialized) {
            tags.add(tag);
        }
    }

    public void evict() {
        if (initialized) {
            cache.removeByTags(tags);
        }
    }

}
