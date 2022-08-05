package com.foros.aspect.registry.find;

import com.foros.aspect.registry.AspectDeclarationInfo;
import com.foros.aspect.util.AspectUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

public final class AspectDeclarationFinder {

    public static Map<Class, AspectDeclarationInfo> findAspectDeclarations(String path) {
        final HashMap<Class, AspectDeclarationInfo> result = new HashMap<Class, AspectDeclarationInfo>();

        ClassTraverser.traverse(path, new ClassTraverser.Listener() {
            @Override
            public void onClass(Class<?> clazz) {
                AspectDeclarationInfo declaration = AspectUtil.findAspectDeclaration(clazz);

                if (declaration != null) {
                    result.put(clazz, declaration);
                }
            }
        });

        return result;
    }

}
