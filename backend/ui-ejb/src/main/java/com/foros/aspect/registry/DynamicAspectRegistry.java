package com.foros.aspect.registry;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.util.AspectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public abstract class DynamicAspectRegistry implements AspectRegistry {

    @Override
    public AspectDescriptor getDescriptor(Class<? extends Annotation> annotation, Method method) {
        AspectInfo aspectInfo = AspectUtil.findAspect(annotation, method);
        if (aspectInfo != null) {
            return getAspectDescriptorFactoryService().create(aspectInfo);
        } else {
            return null;
        }
    }

    protected abstract AspectDescriptorFactoryService getAspectDescriptorFactoryService();


    @Override
    public Map<String, Set<PropertyDescriptor>> getPropertyDescriptors(Class<? extends Annotation> index, Class<?> beanType) {
        throw new UnsupportedOperationException();
    }

}
