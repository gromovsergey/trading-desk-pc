package com.foros.cache.generic.implementor.memcached;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

class LongAsStringTranscoder implements Transcoder<Long> {

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(Long o) {
        return new CachedData(0, String.valueOf(o).getBytes(), getMaxSize());
    }

    @Override
    public Long decode(CachedData d) {
        return Long.parseLong(new String(d.getData()).trim());
    }

    @Override
    public int getMaxSize() {
        return CachedData.MAX_SIZE;
    }
}
