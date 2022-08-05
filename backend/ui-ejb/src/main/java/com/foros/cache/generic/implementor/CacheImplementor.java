package com.foros.cache.generic.implementor;

import java.util.List;
import java.util.Map;
import org.joda.time.ReadableDuration;

public interface CacheImplementor {

    <T> T get(Class<T> type, String key);

    <T> Map<String, T> getAll(Class<T> type, List<String> keys);

    <T> T getOrSet(Class<T> type, String key, T defaultValue, ReadableDuration expirationTime);

    <T> Map<String, T> getOrSetAll(Class<T> type, List<String> keys, T defaultValue, ReadableDuration expirationTime);

    <T> void set(String key, T value, ReadableDuration expirationTime);

    <T> void setAll(Map<String, T> values, ReadableDuration expirationTime);

    void remove(String key);

    void removeAll(List<String> keys);

    void clear();

    long increment(String key, long defaultValue, ReadableDuration expirationTime);

    List<Long> incrementAll(List<String> keys, long defaultValue, ReadableDuration expirationTime);

}
