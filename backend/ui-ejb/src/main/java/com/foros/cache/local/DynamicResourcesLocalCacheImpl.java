package com.foros.cache.local;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton(name = "DynamicResourcesLocalCache")
public class DynamicResourcesLocalCacheImpl extends LocalCacheImpl implements DynamicResourcesLocalCache {
    @PostConstruct
    public void init() {
        setClearInterval(5 * 60); // 5 minutes
    }
}
