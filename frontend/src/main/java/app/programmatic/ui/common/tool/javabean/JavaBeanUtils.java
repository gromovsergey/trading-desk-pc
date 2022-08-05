package app.programmatic.ui.common.tool.javabean;

import app.programmatic.ui.common.model.EntityBase;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class JavaBeanUtils {
    private static final ConcurrentHashMap<BeanAndStopClasses<?>, JavaBeanAccessor> javaBeanAccessors = new ConcurrentHashMap<>();

    public static final <T> JavaBeanAccessor<T> createJavaEntityBeanAccessor(Class<T> javaBeanClass) {
        return createJavaBeanAccessor(javaBeanClass, EntityBase.class);
    }

    public static final <T> JavaBeanAccessor<T> createJavaBeanAccessor(Class<T> javaBeanClass) {
        return createJavaBeanAccessor(javaBeanClass, Object.class);
    }

    public static final <T> JavaBeanAccessor<T> createJavaBeanAccessor(Class<T> javaBeanClass, Class<?> stopClass) {
        BeanAndStopClasses<T> cacheKey = new BeanAndStopClasses<T>(javaBeanClass, stopClass);
        JavaBeanAccessor<T> result = javaBeanAccessors.get(cacheKey);
        if (result == null) {
            JavaBeanAccessor<T> newAccessor = createJavaBeanAccessorImpl(javaBeanClass, stopClass);
            result = javaBeanAccessors.putIfAbsent(cacheKey, newAccessor);
            result = result != null ? result : newAccessor;
        }
        return result;
    }

    private static final <T> JavaBeanAccessor<T> createJavaBeanAccessorImpl(Class<T> javaBeanClass, Class<?> stopClass) {
        HashMap<String, MethodHandleProperty> result = new HashMap<>();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> clazz = javaBeanClass;
        while (!clazz.equals(stopClass) && !clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

                    MethodType getterMethodType = MethodType.methodType(field.getType());
                    MethodHandle getter = lookup.findVirtual(clazz, "get" + fieldName, getterMethodType);

                    MethodType setterMethodType = MethodType.methodType(void.class, field.getType());
                    MethodHandle setter = lookup.findVirtual(clazz, "set" + fieldName, setterMethodType);

                    result.put(field.getName(), new MethodHandleProperty(setter, getter));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    continue;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return new JavaBeanAccessor<>(javaBeanClass, result);
    }

    private static class BeanAndStopClasses<T> {
        private Class<T> javaBeanClass;
        private Class<?> stopClass;

        public BeanAndStopClasses(Class<T> javaBeanClass, Class<?> stopClass) {
            this.javaBeanClass = javaBeanClass;
            this.stopClass = stopClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BeanAndStopClasses<?> that = (BeanAndStopClasses<?>) o;

            if (!javaBeanClass.equals(that.javaBeanClass)) return false;
            return stopClass.equals(that.stopClass);
        }

        @Override
        public int hashCode() {
            int result = javaBeanClass.hashCode();
            result = 31 * result + stopClass.hashCode();
            return result;
        }
    }
}
