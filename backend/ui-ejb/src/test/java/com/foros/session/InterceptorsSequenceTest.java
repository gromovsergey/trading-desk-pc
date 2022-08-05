package com.foros.session;

import com.foros.aspect.registry.find.FindException;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.validation.ValidationInterceptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.interceptor.Interceptors;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public class InterceptorsSequenceTest extends Assert {

    private static final String CLASSPATH = "classpath*:com/foros/**/*.class";

    @Test
    public void testRestrictionValidationInterceptorSequence() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(classLoader);

        Resource[] resources = null;
        try {
            resources = resourceResolver.getResources(CLASSPATH);
        } catch (IOException e) {
            throw new FindException(
                    "An I/O problem occurs when trying to resolve resources matching the pattern: "
                    + CLASSPATH, e);
        }

        List<Class> result = new LinkedList<Class>();

        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        TypeFilter checkAnnotationFilter = new AnnotationTypeFilter(Interceptors.class);

        for (Resource resource : resources) {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                if (checkAnnotationFilter.match(metadataReader, metadataReaderFactory)) {
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> annotatedClass = classLoader.loadClass(className);
                    Interceptors interceptors = annotatedClass.getAnnotation(Interceptors.class);
                    if (interceptors != null) {
                        List<Class> interceptorsClasses = Arrays.asList(interceptors.value());
                        int restrictionPosition = interceptorsClasses.indexOf(RestrictionInterceptor.class);
                        int validationPosition = interceptorsClasses.indexOf(ValidationInterceptor.class);
                        if (restrictionPosition > -1 && validationPosition > -1 && restrictionPosition > validationPosition) {
                            result.add(annotatedClass);
                        }
                    }
                }
            } catch (IOException e) {
                // todo
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (!result.isEmpty()) {
            String message = buildMessage(result);
            fail(message);
        }
    }

    private String buildMessage(List<Class> services) {
        StringBuilder builder = new StringBuilder("Wrong interceptors sequence for classes:\n");
        for (Class service : services) {
            builder.append(service.toString()).append("\n");
        }
        return builder.toString();
    }

}
