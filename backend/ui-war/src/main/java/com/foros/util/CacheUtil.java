package com.foros.util;

import com.foros.cache.NamedCO;

import java.util.Collection;

/**
 * Author: Boris Vanin
 * Date: 11.11.2008
 * Time: 14:57:42
 * Version: 1.0
 */
public class CacheUtil {

    public static <T> boolean contains(Collection<NamedCO<T>> collection, T id) {
        return find(collection, id) != null;
    }

    public static <T> NamedCO<T> find(Collection<NamedCO<T>> collection, T id) {
        for (NamedCO<T> item : collection) {
            if (item.getId().equals(id)) {
                return item;
            }
        }

        return null;
    }

}
