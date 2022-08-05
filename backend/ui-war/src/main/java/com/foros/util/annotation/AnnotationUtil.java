package com.foros.util.annotation;

import com.foros.action.xml.ProcessException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationUtil {
    public static interface Fetcher<A extends Annotation, S, V> {
        V fetch(S source, A annotation);
    }

    public static class FreeFectcher<A extends Annotation, V> implements Fetcher<A, V, V> {
        @Override
        public V fetch(V value, A annotation) {
            return value;
        }
    }

    private static interface Executable<R> {
        R execute() throws Exception;
    }

    private static abstract class AccesableExecutor<T extends AccessibleObject, R> implements Executable<R> {
        protected Object object;

        protected T accessibleObject;

        protected abstract Object safeExecute() throws Exception;

        protected AccesableExecutor(Object o, T accessibleObject) {
            this.object = o;
            this.accessibleObject = accessibleObject;
        }

        @Override
        public R execute() throws Exception {
            boolean oldAccessable = accessibleObject.isAccessible();
            try {
                if (!oldAccessable) {
                    accessibleObject.setAccessible(true);
                }

                return (R) safeExecute();
            } finally {
                accessibleObject.setAccessible(oldAccessable);
            }
        }
    }

    private static class FieldExecutor<R> extends AccesableExecutor<Field, R> {
        private FieldExecutor(Object o, Field field) {
            super(o, field);
        }

        @Override
        protected Object safeExecute() throws Exception {
            return accessibleObject.get(object);
        }

    }

    private static class MethodExecutor<R> extends AccesableExecutor<Method, R> {
        private MethodExecutor(Object o, Method method) {
            super(o, method);
        }

        @Override
        protected Object safeExecute() throws Exception {
            return accessibleObject.invoke(object);
        }
    }

    public static <A extends Annotation, S, V> V fetchAnnotatedValue(Object object, Class<A> annotationClass, Fetcher<A, S, V> fetcher) throws ProcessException {
        try {
            for (Method method : object.getClass().getMethods()) {
                A annotation = method.getAnnotation(annotationClass);
                if (annotation != null) {
                    V value = fetcher.fetch(new MethodExecutor<S>(object, method).execute(), annotation);
                    return value;
                }
            }

            // todo!! view private fields
            for (Field field : object.getClass().getFields()) {
                A annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    return fetcher.fetch(new FieldExecutor<S>(object, field).execute(), annotation);
                }
            }
        } catch (Exception e) {
            throw new ProcessException("Can't find field or method annotated by " + annotationClass.getName() + ", cause: ", e);
        }

        throw new ProcessException("Fields or methods annotated by " + annotationClass.getName() + " not found");
    }

    public static <A extends Annotation, T> T fetchAnnotatedValue(Object object, Class<A> annotationClass) throws ProcessException {
        return fetchAnnotatedValue(object, annotationClass, new FreeFectcher<A, T>());
    }
}
