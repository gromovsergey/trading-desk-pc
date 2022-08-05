package com.foros.aspect.registry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface AspectRegistry {

    AspectDescriptor getDescriptor(Class<? extends Annotation> annotation, Method method);

    Map<String, Set<PropertyDescriptor>> getPropertyDescriptors(Class<? extends Annotation> index, Class<?> beanType);

}