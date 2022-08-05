package com.foros.restriction.registry.find;

import com.foros.aspect.registry.find.FindException;
import com.foros.restriction.annotation.Permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public final class PermissionFinder {

    private String classSearchPattern;

    public PermissionFinder(String classSearchPattern) {
        this.classSearchPattern = classSearchPattern;
    }

    public List<Permissions> find() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(classLoader);

        Resource[] resources;

        try {
            resources = resourceResolver.getResources(classSearchPattern);
        } catch (IOException e) {
            throw new FindException(
                    "An I/O problem occurs when trying to resolve resources matching the pattern: "
                    + classSearchPattern, e);
        }

        List<Permissions> result = new ArrayList<Permissions>();
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        TypeFilter permissionsAnnotationFilter = new AnnotationTypeFilter(Permissions.class);

        for (Resource resource : resources) {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                if(permissionsAnnotationFilter.match(metadataReader, metadataReaderFactory)) {
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> annotatedClass = classLoader.loadClass(className);

                    Permissions permissions = annotatedClass.getAnnotation(Permissions.class);
                    if (permissions != null) {
                        result.add(permissions);
                    }
                }
            } catch (Exception e) {
                throw new FindException("Failed to analyze annotation for resource: " + resource, e);
            }
        }

        return result;
    }

}
