package com.foros.cache.local;

public interface LocalCache {
    Object get(Object key, LocalCacheValuesProducer valuesProducer);

    void clear();
}
