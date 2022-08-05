package com.foros.cache.jbc;

import com.foros.cache.CacheManager;
import com.foros.cache.ForosCache;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.cache.Cache;

/**
 * @author alexey_chernenko
 */
@Stateless(name = "CacheManager")
public class CacheManagerImpl implements CacheManager {

    @Resource (name = "jbossCache")
    private Cache forosJbossCache;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ForosCache getCache(String cacheNodeName) {
        return new ForosCacheImpl(cacheNodeName, forosJbossCache);
    }
}
