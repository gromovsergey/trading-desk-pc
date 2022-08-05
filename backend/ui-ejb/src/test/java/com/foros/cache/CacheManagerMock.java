package com.foros.cache;

public class CacheManagerMock implements CacheManager {
    @Override
    public ForosCache getCache(String cacheNodeName) {
        return new Cache();
    }

    private static class Cache implements ForosCache {

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public Object[] getKeys() {
            return new Object[0];
        }

        public void clear() {
        }
    }
}
