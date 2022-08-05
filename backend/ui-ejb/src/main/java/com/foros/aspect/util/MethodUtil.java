package com.foros.aspect.util;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class MethodUtil {

    private static final String THIS_KEYWORD = "target";
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public static Map<String, Object> createParametersMap(Object target, Method method, Object[] params) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        int index = 0;
        for (String parameter : parameterNames) {
            result.put(parameter, params[index++]);
        }

        if (target != null) {
            result.put(THIS_KEYWORD, target);
        }

        return result;
    }

}
