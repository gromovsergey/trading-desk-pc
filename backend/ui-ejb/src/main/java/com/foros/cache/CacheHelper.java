package com.foros.cache;

import com.foros.session.ServiceLocator;
import com.foros.session.cache.CacheService;

import java.io.Serializable;

public class CacheHelper {

    /**
     * Simple class to do basic eviction based on CacheService methods.
     *
     * @param id - identity of an object to be evicted.
     * @param className - Class.getName() of antity
     * @param inTransaction If : <li><b>true</b> eviction will be performed in the same transaction, but will fail if
     *                      running transaction is marked for rollback. Useful for non-failing scenarios, like
     *                      bulk and usual create/update operations.</li>
     *                      <li><b>false</b>, eviction will be performed in a separate transaction. Used in exceptional
     *                      cases where running transaction is being rolled back and we need to refresh data from
     *                      database, rather then keeping it in a second level cache.</li>
     */
    public static void evict(Serializable id, String className, boolean inTransaction) {
        CacheService cacheService  = ServiceLocator.getInstance().lookup(CacheService.class);
        if (inTransaction) {
            cacheService.evict(className, id);
        } else {
            cacheService.evictNonTransactional(className, id);
        }
    }
}
