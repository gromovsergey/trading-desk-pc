package com.foros.cache.local;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton(name = "LocalizedResourcesLocalCache")
public class LocalizedResourcesLocalCacheImpl extends LocalCacheImpl implements LocalizedResourcesLocalCache {
    @PostConstruct
    public void init() {
        setClearInterval(5 * 60); // 5 minutes
    }
}
