package com.foros.aspect.registry.find;

import com.foros.aspect.registry.MethodDescriptor;
import com.foros.aspect.registry.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AspectDescriptors {

    public static class AspectDescriptorContainer<T, D> {

        private Map<Class<?>, Map<T, Set<D>>> descriptorsContainer = new HashMap<Class<?>, Map<T, Set<D>>>();
        
        public Set<D> get(Class<?> type, T key) {
            Map<T, Set<D>> container = descriptorsContainer.get(type);

            if (container == null) {
                return null;
            }

            return container.get(key);
        }

        public Map<T, Set<D>> get(Class<?> type) {
            return descriptorsContainer.get(type);
        }

        public Set<D> getOrCreate(Class<?> type, T key) {
            Map<T, Set<D>> container = descriptorsContainer.get(type);

            if (container == null) {
                container = new HashMap<T, Set<D>>();
                descriptorsContainer.put(type, container);
            }

            Set<D> descriptors = container.get(key);

            if (descriptors == null) {
                descriptors = new HashSet<D>();
                container.put(key, descriptors);
            }

            return descriptors;
        }

        public boolean has(Class<?> type, T key) {
            Map<T, Set<D>> container = descriptorsContainer.get(type);
            if (container == null) {
                return false;
            }
            
            Set<D> descriptors = container.get(key);
            
            return descriptors != null && !descriptors.isEmpty();
        }

        public void add(Class<?> type, T key, D descriptor) {
            getOrCreate(type, key).add(descriptor);
        }

        public void add(Class<?> type, T key, Set<D> descriptors) {
            getOrCreate(type, key).addAll(descriptors);
        }

        public boolean isEmpty() {
            return descriptorsContainer.isEmpty();
        }

    }

    public static class ClassDescriptors {
        private AspectDescriptorContainer<Method, MethodDescriptor> methodAspects
                = new AspectDescriptorContainer<Method, MethodDescriptor>();

        private AspectDescriptorContainer<String, PropertyDescriptor> propertyAspects
                = new AspectDescriptorContainer<String, PropertyDescriptor>();

        public AspectDescriptorContainer<Method, MethodDescriptor> method() {
            return methodAspects;
        }

        public AspectDescriptorContainer<String, PropertyDescriptor> property() {
            return propertyAspects;
        }

    }

    private Map<Class<? extends Annotation>, ClassDescriptors> aspectDescriptors
            = new HashMap<Class<? extends Annotation>, ClassDescriptors>();

    public ClassDescriptors get(Class<? extends Annotation> index) {
        ClassDescriptors classDescriptors = aspectDescriptors.get(index);

        if (classDescriptors == null) {
            classDescriptors = new ClassDescriptors();
            aspectDescriptors.put(index, classDescriptors);
        }

        return classDescriptors;
    }
}
