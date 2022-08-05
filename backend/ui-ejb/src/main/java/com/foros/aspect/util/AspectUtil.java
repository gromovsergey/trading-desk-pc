package com.foros.aspect.util;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.AspectReader;
import com.foros.aspect.NameReader;
import com.foros.aspect.annotation.Aspect;
import com.foros.aspect.annotation.AspectDeclaration;
import com.foros.aspect.registry.AspectDeclarationInfo;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AspectUtil {

    private static final Map<Class<?>, Map<Annotation, ?>> CACHE = new ConcurrentHashMap<>();
    private static final AnnotationChain<?> NULL_CHAIN = new AnnotationChain<>(null);

    public static Set<AspectInfo> findAspects(Method method) {
        return findAspectsImpl(method.getAnnotations());
    }

    public static Set<AspectInfo> findAspects(Field field) {
        return findAspectsImpl(field.getAnnotations());
    }

    private static Set<AspectInfo> findAspectsImpl(Annotation[] annotations) {
        HashSet<AspectInfo> result = new HashSet<AspectInfo>();
        for (Annotation annotation : annotations) {
            AnnotationChain<Aspect> aspect = findMetaAnnotationWithCache(annotation, Aspect.class);
            if (aspect != null) {
                result.add(fetchAspectInfo(aspect));
            }
        }

        return result;
    }

    public static AspectInfo findAspect(Class<? extends Annotation> a, Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            AnnotationChain<Aspect> aspect = findMetaAnnotationWithCache(annotation, Aspect.class);
            if (aspect != null && a.isAssignableFrom(aspect.getBottom().index())) {
                return fetchAspectInfo(aspect);
            }
        }

        return null;
    }

    public static AspectDeclarationInfo findAspectDeclaration(Method method) {
        return findAspectDeclarationInfo(method.getAnnotations());
    }

    public static AspectDeclarationInfo findAspectDeclaration(Class type) {
        return findAspectDeclarationInfo(type.getAnnotations());
    }

    private static AspectDeclarationInfo findAspectDeclarationInfo(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            AnnotationChain<AspectDeclaration> aspectDeclaration = findMetaAnnotationWithCache(annotation, AspectDeclaration.class);
            if (aspectDeclaration != null) {
                return new AspectDeclarationInfo(annotation, aspectDeclaration.getBottom().index(),
                        fetchName(annotation, aspectDeclaration.getBottom()), aspectDeclaration.getBottom().convention());
            }
        }

        return null;
    }

    private static <T extends Annotation> AnnotationChain<T> findMetaAnnotationWithCache(Annotation annotation, Class<T> type) {
        Map<Annotation, AnnotationChain<T>> cachedType = (Map<Annotation, AnnotationChain<T>>) CACHE.get(type);

        if (cachedType == null) {
            cachedType = new ConcurrentHashMap<>();
            CACHE.put(type, cachedType);
        }

        AnnotationChain<T> metaAnnotation = cachedType.get(annotation);
        if (metaAnnotation == null) {
            metaAnnotation = findMetaAnnotation(annotation, type);
            if (metaAnnotation != null) {
                metaAnnotation.chain(annotation);
            }  else {
                metaAnnotation = (AnnotationChain<T>) NULL_CHAIN;
            }
            cachedType.put(annotation, metaAnnotation);
        }

        return metaAnnotation == NULL_CHAIN ? null : metaAnnotation;
    }

    private static <T extends Annotation> AnnotationChain<T> findMetaAnnotation(Annotation annotation, Class<T> type) {
        if(annotation.annotationType() == Target.class ||
                annotation.annotationType() == Retention.class ||
                annotation.annotationType() == Documented.class) {
            return null;
        }

        Annotation[] typeAnnotations = annotation.annotationType().getAnnotations();
        for (Annotation typeAnnotation : typeAnnotations) {
            if (type.isAssignableFrom(typeAnnotation.getClass())) {
                return new AnnotationChain<T>(type.cast(typeAnnotation));
            }

            // todo!!! check this recursion!!
            AnnotationChain<T> metaAnnotationChain = findMetaAnnotation(typeAnnotation, type);

            if (metaAnnotationChain != null) {
                return metaAnnotationChain.chain(typeAnnotation);
            }
        }

        return null;
    }

    private static AspectInfo fetchAspectInfo(AnnotationChain<Aspect> aspect) {
        Class<? extends AspectReader> aspectReaderClass = aspect.getBottom().reader();
        AspectReader aspectReader = createInstance(aspectReaderClass);
        return aspectReader.read(aspect.getPreBottom(), aspect);
    }

    public static String fetchName(Annotation annotation, AspectDeclaration aspectDeclaration) {
        Class<? extends NameReader> reader = aspectDeclaration.reader();
        NameReader nameReader = createInstance(reader);
        return nameReader.read(annotation);
    }

    private static <T> T createInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
