package com.foros.cache;

/**
 * Thread safe reference for caching
 */
public interface ForosCache {
    /**
     * Returns data held under specified key in cache
     *
     * @param key key to fetch
     * @return value, or null if the key does not exist.
     * @throws IllegalStateException if the cache is not in a started state
     */
    Object get(Object key);

     /**
    * Associates the specified value with the specified key for this cache.
    * If the Cache previously contained a mapping for this key, the old value is replaced by the specified value.
    *
    * @param key   key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    * @return previous value associated with specified key, or <code>null</code> if there was no mapping for key.
    *         A <code>null</code> return can also indicate that the Cache previously associated <code>null</code> with the specified key, if the implementation supports null values.
    * @throws IllegalStateException if the cache is not in a started state.
    */
    Object put(Object key, Object value);

   /**
    * Removes the mapping for this key from a Cache.
    * Returns the value to which the Cache previously associated the key, or
    * <code>null</code> if the Cache contained no mapping for this key.
    *
    * @param key   key with which the specified value is to be associated.
    * @return previous value associated with specified key, or <code>null</code> if there was no mapping for key.
    *         A <code>null</code> return can also indicate that the Cache previously associated <code>null</code> with the specified key, if the implementation supports null values.
    * @throws IllegalStateException if the cache is not in a started state.    Object put(Object key, Object value);
    */
    Object remove(Object key);


    /**
    * Returns an array of attribute keys.
    * Returns empty array if the node is not found, otherwise array containing keys.
	* The array is a copy of the actual keys.
    * <p/>
    * @throws IllegalStateException if the cache is not in a started state
    */
    Object[] getKeys();

    void clear();
}
