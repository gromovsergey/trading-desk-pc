package com.foros.cache.generic.implementor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.ReadableDuration;

public class NullCacheImplementor implements CacheImplementor {
    @Override
    public <T> T get(Class<T> type, String key) {
        return null;
    }

    @Override
    public <T> Map<String, T> getAll(Class<T> type, List<String> keys) {
        return Collections.emptyMap();
    }

    @Override
    public <T> T getOrSet(Class<T> type, String key, T defaultValue, ReadableDuration expirationTime) {
        return null;
    }

    @Override
    public <T> Map<String, T> getOrSetAll(Class<T> type, List<String> keys, T defaultValue, ReadableDuration expirationTime) {
        return Collections.emptyMap();
    }

    @Override
    public <T> void set(String key, T value, ReadableDuration expirationTime) {
    }

    @Override
    public <T> void setAll(Map<String, T> values, ReadableDuration expirationTime) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public void removeAll(List<String> keys) {
    }

    @Override
    public void clear() {
    }

    @Override
    public long increment(String key, long defaultValue, ReadableDuration expirationTime) {
        return defaultValue;
    }

    @Override
    public List<Long> incrementAll(List<String> keys, long defaultValue, ReadableDuration expirationTime) {
        return Collections.emptyList();
    }
}
