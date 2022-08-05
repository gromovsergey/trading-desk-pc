package com.foros.cache.generic.implementor.memcached;

import com.foros.cache.generic.implementor.CacheImplementor;
import com.foros.cache.generic.serializer.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import org.joda.time.DateTimeConstants;
import org.joda.time.ReadableDuration;

public class MemcachedCacheImplementor implements CacheImplementor {

    private final TranscoderRegistry transcoderRegistry;
    private final MemcachedClient client;

    public MemcachedCacheImplementor(MemcachedClient client, Serializer serializer) {
        this.client = client;
        this.transcoderRegistry = new TranscoderRegistry(serializer);
    }

    @Override
    public <T> T getOrSet(Class<T> type, String key, T defaultValue, ReadableDuration expirationTime) {
        T value = get(type, key);
        if (value == null) {
            value = defaultValue;
            set(key, value, expirationTime);
        }
        return value;
    }

    @Override
    public <T> Map<String, T> getOrSetAll(Class<T> type, List<String> keys, T defaultValue, ReadableDuration expirationTime) {
        Map<String, T> result = new HashMap<String, T>( getAll(type, keys) );

        for (String key : keys) {
            if (!result.containsKey(key)) {
                set(key, defaultValue, expirationTime);
                result.put(key, defaultValue);
            }
        }

        return result;
    }

    @Override
    public <T> void setAll(Map<String, T> values, ReadableDuration expirationTime) {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue(), expirationTime);
        }
    }

    @Override
    public void removeAll(List<String> keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    @Override
    public List<Long> incrementAll(List<String> keys, long defaultValue, ReadableDuration expirationTime) {
        ArrayList<Long> result = new ArrayList<Long>();
        for (String key : keys) {
            result.add(increment(key, defaultValue, expirationTime));
        }
        return result;
    }

    @Override
    public <T> T get(Class<T> type, String key) {
        return client.get(key, transcoderRegistry.get(type));
    }

    @Override
    public <T> Map<String, T> getAll(Class<T> type, List<String> keys) {
        return client.getBulk(keys, transcoderRegistry.get(type));
    }

    @Override
    public <T> void set(String key, T value, ReadableDuration expirationTime) {
        client.set(key, exp(expirationTime),
                value, transcoderRegistry.get((Class<T>) value.getClass()));
    }

    @Override
    public void remove(String key) {
        client.delete(key);
    }

    @Override
    public void clear() {
        client.flush();
    }

    @Override
    public long increment(String key, long defaultValue, ReadableDuration expirationTime) {
        return client.incr(key, 1, defaultValue, exp(expirationTime));
    }

    private int exp(ReadableDuration expirationTime) {
        return divide(expirationTime.getMillis(), DateTimeConstants.MILLIS_PER_SECOND);
    }

    private static int divide( long n, long d ){
        n = -n;
        d = -d;
        long tweak = (n >>> (Long.SIZE-1) ) - 1;
        return (int) ((n + tweak) / d + tweak);
    }
}
