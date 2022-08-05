package com.foros.aspect.registry.find;

import com.foros.aspect.AspectInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Aspects {

    public static class ClassAspects {
        private AspectContainer<Field> fieldAspects = new AspectContainer<Field>();
        private AspectContainer<Method> methodAspects = new AspectContainer<Method>();

        public AspectContainer<Field> fields() {
            return fieldAspects;
        }
        
        public AspectContainer<Method> methods() {
            return methodAspects;
        }
        
        public boolean notEmpty() {
            return !fieldAspects.isEmpty() || !methodAspects.isEmpty();
        }
    }
    
    public static interface AspectListener<T> {
        void onValue(Class<?> type, T t, Set<AspectInfo> infos);
    }
    
    private Map<Class<?>, ClassAspects> aspects = new HashMap<Class<?>, ClassAspects>();

    public boolean has(Class<?> type) {
        ClassAspects classAspects = aspects.get(type);
        return classAspects != null && classAspects.notEmpty();
    }

    public ClassAspects get(Class<?> type) {
        ClassAspects classAspects = aspects.get(type);
        if (classAspects == null) {
            classAspects = new ClassAspects();
            aspects.put(type, classAspects);
        }
        
        return classAspects;
    }
    
    public void iterateFields(AspectListener<Field> fieldListener) {
        for (Map.Entry<Class<?>, ClassAspects> entry : aspects.entrySet()) {
            entry.getValue().fields().iterate(entry.getKey(), fieldListener);
        }
    }

    public void iterateMethods(AspectListener<Method> methodListener) {
        for (Map.Entry<Class<?>, ClassAspects> entry : aspects.entrySet()) {
            entry.getValue().methods().iterate(entry.getKey(), methodListener);
        }
    }

}
