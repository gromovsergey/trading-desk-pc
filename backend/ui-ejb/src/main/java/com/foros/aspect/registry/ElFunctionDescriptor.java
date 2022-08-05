package com.foros.aspect.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElFunctionDescriptor {

    private Class<?> containerClass;
    protected List<Method> functionMethods = new ArrayList<Method>();

    public ElFunctionDescriptor(Class<?> containerClass) {
        this.containerClass = containerClass;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public List<Method> getFunctionMethods() {
        return Collections.unmodifiableList(functionMethods);
    }

    void addFunctionMethod(Method method) {
        functionMethods.add(method);
    }

}
