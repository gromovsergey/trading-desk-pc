package com.foros.cache.generic;

import javax.ejb.Local;

@Local
public interface CacheProviderService {

    Cache getCache();

    void touchTag(Object tag);

}
