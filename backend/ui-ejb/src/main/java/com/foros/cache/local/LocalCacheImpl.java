package com.foros.cache.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

public abstract class LocalCacheImpl implements LocalCache {
    private final Map<Object, Object> cache = new ConcurrentHashMap<>();
    private int clearIntervalMillis;

    private volatile long lastClearTimeMillis;

    public LocalCacheImpl() {
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object get(Object key, LocalCacheValuesProducer valuesProducer) {
        // check, is it time to clear the cache
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - lastClearTimeMillis) > clearIntervalMillis) {
            lastClearTimeMillis = currentTimeMillis;
            cache.clear();
        }

        Object value = cache.get(key);
        if (value != null) {
            return value;
        }

        try {
            value = valuesProducer.getValue();
            cache.put(key, value);
        } catch (Exception ex) {
            value = null;
        }
        return value;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void clear() {
        lastClearTimeMillis = System.currentTimeMillis();
        cache.clear();
    }

    /** In seconds */
    protected void setClearInterval(int clearInterval) {
        clearIntervalMillis = clearInterval * 1000;
    }
}
