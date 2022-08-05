package com.foros.aspect.registry.find;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class ClassTraverser {

    private final static Logger logger = Logger.getLogger(ClassTraverser.class.getName());

    private ClassTraverser() {
    }

    public static interface Listener {
        void onClass(Class<?> clazz);
    }

    public static void traverse(String classSearchPattern, Listener listener) {
        traverse(classSearchPattern, new AlwaysMatchTypeFilter(), listener);
    }
    
    public static void traverse(String classSearchPattern, TypeFilter typeFilter, Listener listener) {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(classLoader);

        Resource[] resources = null;
        try {
            resources = resourceResolver.getResources(classSearchPattern);
        } catch (IOException e) {
            throw new FindException(
                    "An I/O problem occurs when trying to resolve resources matching the pattern: "
                            + classSearchPattern, e);
        }

        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        for (Resource resource : resources) {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                if (typeFilter.match(metadataReader, metadataReaderFactory)) {
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> annotatedClass = classLoader.loadClass(className);

                    listener.onClass(annotatedClass);
                }
            } catch (NoClassDefFoundError e) {
                logger.warning("Failed to analyze annotation for resource: " + e.getMessage());
            } catch (Exception e) {
                logger.warning("Failed to analyze annotation for resource: " + e.getMessage());
                // throw new FindException("Failed to analyze annotation for resource: " + resource, e);
            }
        }
    }

    private static class AlwaysMatchTypeFilter implements TypeFilter {
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            return true;
        }
    }
}
