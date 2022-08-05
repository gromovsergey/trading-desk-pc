package com.foros.aspect.registry.find;

import com.foros.aspect.annotation.ElFunction;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public final class ElFunctionFinder {

    public static Map<Class<?>, List<Method>> findElFunctions(String path) {
        final HashMap<Class<?>, List<Method>> result = new HashMap<Class<?>, List<Method>>();

        ClassTraverser.traverse(path, new AnnotationTypeFilter(Local.class), new ClassTraverser.Listener() {
            @Override
            public void onClass(Class<?> clazz) {
                final List<Method> methods = new ArrayList<Method>();

                ReflectionUtils.doWithMethods(clazz,
                        new ReflectionUtils.MethodCallback() {
                            @Override
                            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                                methods.add(method);
                            }
                        },
                        new ReflectionUtils.MethodFilter() {
                            @Override
                            public boolean matches(Method method) {
                                return method.getAnnotation(ElFunction.class) != null;
                            }
                        }
                );

                if (!methods.isEmpty()) {
                    result.put(clazz, methods);
                }
            }
        });

        return result;
    }

}