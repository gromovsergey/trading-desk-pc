package com.foros.cache.jbc;

import com.foros.cache.ForosCache;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.lock.TimeoutException;

/**
 * Wrapper class for jboss cache.
 * @author alexey_chernenko
 */
public class ForosCacheImpl implements ForosCache {

    private static final Logger logger = Logger.getLogger(ForosCacheImpl.class.getName());

    private String cacheName;
    private Cache<Object, Object> cache;

    public ForosCacheImpl(String cacheName, Cache<Object, Object> owningCache) {
        this.cacheName = cacheName;
        this.cache = owningCache;
    }

    public Object get(Object key) {
        return cache.get(Fqn.fromString(cacheName), key);
    }

    public Object put(Object key, Object value) {
        Object r = null;
        try {
            r = cache.put(Fqn.fromString(cacheName), key, value);
        } catch (TimeoutException e) {
            logger.warning("failed to put data to Jboss Cache: " + e.toString() + ": " + e.getMessage());
        }
        return r;
    }

    public Object remove(Object key) {
        return cache.remove(Fqn.fromString(cacheName), key);
    }

    public Object[] getKeys() {

        Set<Object> keys = cache.getKeys(Fqn.fromString(cacheName));

        if(keys == null) {
            keys = new HashSet<Object>();
        }

        return keys.toArray();
    }

    @Override
    public void clear() {
        cache.clearData(Fqn.fromString(cacheName));
    }
}
