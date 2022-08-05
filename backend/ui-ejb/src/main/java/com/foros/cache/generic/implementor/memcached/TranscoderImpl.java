package com.foros.cache.generic.implementor.memcached;

import com.foros.cache.generic.serializer.Serializer;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

class TranscoderImpl<T> implements Transcoder<T> {
    private final Class<T> type;
    private final Serializer serializer;

    public TranscoderImpl(Class<T> type, Serializer serializer) {
        this.type = type;
        this.serializer = serializer;
    }

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(T o) {
        return new CachedData(0, serializer.serialize(o), getMaxSize());
    }

    @Override
    public T decode(CachedData d) {
        return serializer.deserialize(type, d.getData());
    }

    @Override
    public int getMaxSize() {
        return CachedData.MAX_SIZE;
    }
}
