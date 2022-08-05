package com.foros.persistence.hibernate;

import com.foros.cache.generic.interceptor.annotations.Evictable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.AnnotationUtils;

class EvictableAnnotationHelper {

    private static Map<Class<?>, Boolean> cachedIsEvictable = new ConcurrentHashMap<>();

    public static boolean isEvictable(Class<?> entityClass) {
        Boolean res = cachedIsEvictable.get(entityClass);
        if (res == null) {
            res = AnnotationUtils.findAnnotation(entityClass, Evictable.class) != null;
            cachedIsEvictable.put(entityClass, res);
        }
        return res;
    }

}
