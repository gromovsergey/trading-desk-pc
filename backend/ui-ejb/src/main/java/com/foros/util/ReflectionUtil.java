package com.foros.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static Class<?> getActualTypeArgument(Class<?> clazz, int i) {
        try {
            Type superclass = clazz.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                Type[] ata = ((ParameterizedType) superclass).getActualTypeArguments();
                return (Class<?>) ata[i];
            } else {
                return getActualTypeArgument((Class<?>) superclass, i);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
