package com.foros.aspect.registry.find;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.util.AspectUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public final class AspectFinder {

    private AspectFinder() {
    }

    public static Aspects findAspects(String classSearchPattern) {
        Aspects aspects = new Aspects();

        ClassTraverser.traverse(classSearchPattern, new CollectAspectsListener(aspects));

        return aspects;
    }


    private static class CollectAspectsListener implements ClassTraverser.Listener {
        private Aspects aspects;

        public CollectAspectsListener(Aspects aspects) {
            this.aspects = aspects;
        }

        @Override
        public void onClass(Class<?> clazz) {
            processFields(clazz);
            processMethods(clazz);
        }

        private void processMethods(Class<?> clazz) {
            final AspectContainer<Method> methodAspectContainer = aspects.get(clazz).methods();

            ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    Set<AspectInfo> aspectInfos = AspectUtil.findAspects(method);
                    if (!aspectInfos.isEmpty()) {
                        methodAspectContainer.add(method, aspectInfos);
                    }
                }
            });
        }

        private void processFields(Class<?> clazz) {
            final AspectContainer<Field> fieldAspectContainer = aspects.get(clazz).fields();

            ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    Set<AspectInfo> aspectInfos = AspectUtil.findAspects(field);
                    if (!aspectInfos.isEmpty()) {
                        fieldAspectContainer.add(field, aspectInfos);
                    }
                }
            });
        }
    }

}