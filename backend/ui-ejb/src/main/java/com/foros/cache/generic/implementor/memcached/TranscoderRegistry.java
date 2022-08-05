package com.foros.cache.generic.implementor.memcached;

import com.foros.cache.generic.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.spy.memcached.transcoders.Transcoder;

class TranscoderRegistry {

    private ConcurrentMap<Class<?>, Transcoder<?>> transcoders = new ConcurrentHashMap<Class<?>, Transcoder<?>>();

    private Serializer serializer;

    public TranscoderRegistry(Serializer serializer) {
        this.serializer = serializer;
        initDefaultTranscoders();
    }

    private synchronized void initDefaultTranscoders() {
        transcoders.put(Long.class, new LongAsStringTranscoder());
    }

    public <T> Transcoder<T> get(Class<T> type) {
        Transcoder<T> transcoder = getImpl(type);

        if (transcoder == null) {
            transcoder = createAndPutImpl(type);
        }

        return transcoder;
    }

    private <T> Transcoder<T> getImpl(Class<T> type) {
        return (Transcoder<T>) transcoders.get(type);
    }

    private <T> Transcoder<T> createAndPutImpl(Class<T> type) {
        Transcoder<T> transcoder = new TranscoderImpl<T>(type, serializer);
        transcoders.putIfAbsent(type, transcoder);
        return transcoder;
    }
}
