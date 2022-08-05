package com.foros.aspect.registry;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class AspectDeclarationDescriptor {

    private Class<?> serviceClass;
    private String name;
    private Collection<Method> methods;

    public AspectDeclarationDescriptor(Class<?> serviceClass, String name, Collection<Method> methods) {
        this.serviceClass = serviceClass;
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public Method getMethod(Object[] params) {
        Method result = getMethodSafe(params);

        if (result == null) {
            throw new IllegalStateException(
                    String.format("[Target: %1$s]: Method with passed parameters %2$s not found!",
                    getName(), Arrays.toString(params)));
        }

        return result;
    }

    public Method getMethodSafe(Object[] params) {
        Method result = null;

        for (Method method : methods) {
            if (isCompatible(params, method.getParameterTypes())) {
                if (result != null) {
                    throw new IllegalStateException(
                           String.format(
                                   "[Aspect: %1$s]: More than one method \"%2$s\" compatible with this parameters %3$s!",
                                   getName(), method.getName(), Arrays.toString(method.getParameterTypes())));
                } else {
                    result = method;
                }
            }
        }

        return result;
    }

    private boolean isCompatible(Object[] params, Class[] types) {
        if (params.length != types.length) {
            return false;
        }

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            Class<?> type = types[i];

            boolean isValueNull = value == null;

            if (type.isPrimitive() && isValueNull) {
                return false;
            }

            if (!isValueNull && !type.isAssignableFrom(value.getClass())) {
                return false;
            }
        }

        return true;
    }

}
