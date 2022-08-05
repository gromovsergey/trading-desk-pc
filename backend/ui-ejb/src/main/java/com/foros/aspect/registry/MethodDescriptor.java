package com.foros.aspect.registry;

import java.lang.reflect.Method;

public class MethodDescriptor {

    private Method method;
    private AspectDescriptor aspectDescriptor;

    public MethodDescriptor(Method method, AspectDescriptor aspectDescriptor) {
        this.method = method;
        this.aspectDescriptor = aspectDescriptor;
    }

    public Object invoke(Object bean, Object...params) {
        return invokeMethod(bean, params);
    }

    public Method getMethod() {
        return method;
    }

    public AspectDescriptor getAspectDescriptor() {
        return aspectDescriptor;
    }

    private Object invokeMethod(Object bean, Object...params) {
        try {
            return method.invoke(bean, params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
