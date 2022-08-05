package com.foros.cache.generic;

import java.util.Collection;

public interface CacheRegion {
    <T, K> T get(K key);

    <T, K> T get(K key, CreateValueCallback<T, K> callback);

    <T, K> T get(K key, Collection<?> tags, CreateValueCallback<T, K> callback);

    <T> void set(Object key, T value);

    <T> void set(Object key, T value, Collection<?> tags);

    void remove(Object key);
}
