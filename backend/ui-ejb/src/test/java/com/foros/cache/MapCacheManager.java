package com.foros.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;

/**
 * Author: Boris Vanin
 */
@Stateless(name = "CacheManager")
public class MapCacheManager implements CacheManager {

    private static final Map<Object, Object> cache = Collections.synchronizedMap(new HashMap<Object, Object>());

    public ForosCache getCache(String cacheNodeName) {
        return new ForosCache() {
            public Object get(Object key) {
                return cache.get(key);
            }

            public Object put(Object key, Object value) {
                return cache.put(key, value);
            }

            public Object remove(Object key) {
                return cache.remove(key);
            }

            public Object[] getKeys() {
                return cache.keySet().toArray();
            }

            public void clear() {
                cache.clear();
            }
        };
    }
}
