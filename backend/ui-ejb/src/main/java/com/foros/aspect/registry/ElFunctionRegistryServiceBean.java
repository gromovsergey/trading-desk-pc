package com.foros.aspect.registry;

import com.foros.aspect.AspectException;
import com.foros.aspect.annotation.ElFunction;
import com.foros.aspect.registry.find.ElFunctionFinder;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

@Stateless(name = "ElFunctionRegistryService")
public class ElFunctionRegistryServiceBean implements ElFunctionRegistryService {

    private static final String CLASS_SEARCH_PATTERN = "classpath*:com/foros/**/*.class";

    private static final Logger logger = Logger.getLogger(ElFunctionRegistryServiceBean.class.getName());

    private Map<ElFunction.Namespace, Map<String, ElFunctionDescriptor>> expressionFunctions = null;

    @PostConstruct
    public void init() {
        if (expressionFunctions == null) {
            expressionFunctions = findElFunctions();
        }
    }

    private Map<ElFunction.Namespace, Map<String, ElFunctionDescriptor>> findElFunctions() {
        HashMap<ElFunction.Namespace, Map<String, ElFunctionDescriptor>> result =
                new HashMap<ElFunction.Namespace, Map<String, ElFunctionDescriptor>>();

        Map<Class<?>, List<Method>> expressionFunctions = ElFunctionFinder.findElFunctions(CLASS_SEARCH_PATTERN);
        for (Map.Entry<Class<?>, List<Method>> entry : expressionFunctions.entrySet()) {
            for (Method method : entry.getValue()) {
                Class<?> clazz = entry.getKey();
                addElFunction(result, clazz, method);
                logger.fine("ElFunction registered: " + method.toString());
            }
        }

        return result;
    }

    private void addElFunction(
            HashMap<ElFunction.Namespace, Map<String, ElFunctionDescriptor>> result,
            Class<?> clazz, Method method)
    {
        ElFunction methodAnnotation = method.getAnnotation(ElFunction.class);

        ElFunction.Namespace[] namespaces = methodAnnotation.namespaces();

        for (ElFunction.Namespace namespace : namespaces) {

            Map<String, ElFunctionDescriptor> functions = result.get(namespace);
            if (functions == null) {
                functions = new HashMap<String, ElFunctionDescriptor>();
                result.put(namespace, functions);
            }

            String name = methodAnnotation.name().equals("") ? method.getName() : methodAnnotation.name();

            ElFunctionDescriptor functionMeta = functions.get(name);
            if (functionMeta == null) {
                functionMeta = new ElFunctionDescriptor(clazz);
                functions.put(name, functionMeta);
            }

            functionMeta.addFunctionMethod(method);
        }
    }

    @Override
    public ElFunctionDescriptor getDescriptor(String name, ElFunction.Namespace namespace) {
        Map<String, ElFunctionDescriptor> functions = expressionFunctions.get(namespace);

        if (functions == null) {
            throw new AspectException("Can't find any function in namespace: " + namespace);
        }

        ElFunctionDescriptor descriptor = functions.get(name);

        if (descriptor == null) {
            throw new AspectException("Can't find function: " + name + " in namespace: " + namespace);
        }

        return descriptor;
    }

}
