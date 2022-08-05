package app.programmatic.ui.common.aspect.restriction;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import app.programmatic.ui.common.error.ForbiddenException;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.restriction.service.LocalRestrictionService;

import java.util.*;

@Aspect
@Order(2)
@Component
public class RestrictionAspect {

    @Autowired
    private LocalRestrictionService restrictionService;

    @Before("execution(public * *(..)) && " +
            "@within(org.springframework.stereotype.Service) && " +
            "@annotation(restrict)")
    public void intercept(JoinPoint jp, Restrict restrict) {
        Object[] args = getFilteredArguments(jp, restrict.parameters());
        if (!restrictionService.isAllowed(restrict.restriction(), args)) {
            throw new ForbiddenException();
        }
    }

    private Object[] getFilteredArguments(JoinPoint jp, String[] parameters) {
        if (parameters.length == 0) {
            return jp.getArgs();
        }

        Map<String, Object> argumentsMap = getArgumentsByNamesMap(jp);
        List<Object> result = new ArrayList<>(argumentsMap.size());
        for (String parameter : parameters) {
            String[] parts = parameter.split("\\.");

            if (parts.length > 2) {
                throw new IllegalArgumentException("Incorrect parameter: " + parameter + ". Depth > 1 is not supported.");
            }

            if (!argumentsMap.containsKey(parts[0])) {
                throw new IllegalArgumentException("Incorrect parameter: " + parameter + ". Can't find name in method signature.");
            }

            if (parts.length == 1) {
                result.add(argumentsMap.get(parts[0]));
            } else {
                result.add(fetchBeanProperty(argumentsMap.get(parts[0]), parts[1]));
            }
        }

        return result.toArray();
    }

    private static Map<String, Object> getArgumentsByNamesMap(JoinPoint jp) {
        String[] argumentNames = ((MethodSignature) jp.getSignature()).getParameterNames();
        Iterator<String> argumentNamesIterator = Arrays.asList(argumentNames).iterator();

        return Arrays.stream(jp.getArgs())
                .collect(HashMap::new, (m,v) -> m.put(argumentNamesIterator.next(), v), HashMap::putAll);
    }

    private static Object fetchBeanProperty(Object bean, String propertyName) {
        try {
            return PropertyUtils.getProperty(bean, propertyName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect parameter.", e);
        }
    }
}
