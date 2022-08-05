package com.foros.util;

import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Class improves perfomance of retreiving annotations from class.
 * It caches previusly extracted information to overcome reflection overloading
 *
 * @author alexey_chernenko
 */
public abstract class AnnotationUtil {
    // Holders per class
    private static final WeakHashMap<String, Reference<AnnotationHolder>> HOLDERS = new WeakHashMap<String, Reference<AnnotationHolder>>();

   public static boolean isAnnotationPresent(final Class klazz, final  Class<? extends Annotation> annotation) {
       return getHolder(klazz).isAnnotationPresent(annotation);
   }

    public static boolean isAnnotationPresent(final Class klazz, final Field field, final Class<? extends Annotation> annotation) {
        return getHolder(klazz).isAnnotationPresent(field, annotation);
   }


   public static AnnotationHolder getHolder(final Class klass) {
        AnnotationHolder holder;
        Reference<AnnotationHolder> value = HOLDERS.get(klass.getName());
        if (value == null) {
            synchronized (HOLDERS) {
                holder = new AnnotationHolder(klass);
                HOLDERS.put(klass.getName(), new WeakReference<AnnotationHolder>(holder));//holder strong refs its own key in the weakhahsmap..
            }
        } else {
            holder = value.get();
            if (holder == null) {//WeakReference content can be null
                synchronized (HOLDERS) {
                    holder = new AnnotationHolder(klass);
                    HOLDERS.put(klass.getName(), new WeakReference<AnnotationHolder>(holder));
                }
            }
        }
        return holder;
    }

    public static Object[] findValuesByParameterAnnotation(Class<?> annotationType, Method method, Object[] values) {
        ArrayList<Object> result = new ArrayList<Object>();

        Annotation[][] parametersAnnotations = method.getParameterAnnotations();

        int index = 0;
        for (Annotation[] parameterAnnotation : parametersAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation.annotationType() == annotationType) {
                    result.add(values[index]);
                }
            }
            index++;
        }

        return result.toArray();
    }

    // Holder class, contains cached information about annotations
    private static class AnnotationHolder {
        private Class klass;
        private Map<String, Boolean> classAnnotationsCache = new HashMap<String, Boolean>();
        private Map<String, Map<String, Boolean>> fieldAnnotationsCache = new HashMap<String, Map<String, Boolean>>();

        private AnnotationHolder(Class klass) {
            this.klass = klass;
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
            String key = annotation.getName();
            Boolean value = classAnnotationsCache.get(key);
            if (value == null) {
                value = klass.isAnnotationPresent(annotation);
                classAnnotationsCache.put(key, value);
            }
            return value;
        }

        public boolean isAnnotationPresent(Field field, Class<? extends Annotation> annotation) {
            Map<String, Boolean> fieldAnnots = fieldAnnotationsCache.get(field.getName()); // should we user compisite string key instead?
            if (fieldAnnots == null) {
                fieldAnnots = new HashMap<String, Boolean>();
                fieldAnnotationsCache.put(field.getName(), fieldAnnots);
            }

            Boolean value = fieldAnnots.get(annotation.getName());
            if (value == null) {
                value = field.isAnnotationPresent(annotation);
                fieldAnnots.put(annotation.getName(), value);
            }
            return value;
        }
    }
}
