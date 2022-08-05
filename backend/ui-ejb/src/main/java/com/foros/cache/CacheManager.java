package com.foros.cache;

import javax.ejb.Local;

/**
 * @author alexey_chernenko
 */
@Local
public interface CacheManager {
    public ForosCache getCache(String cacheNodeName);
}
