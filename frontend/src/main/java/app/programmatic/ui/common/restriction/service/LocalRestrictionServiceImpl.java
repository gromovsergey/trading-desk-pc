package app.programmatic.ui.common.restriction.service;

import static app.programmatic.ui.common.tool.reflection.ReflectionHelper.invokeMethod;
import static app.programmatic.ui.common.tool.reflection.ReflectionHelper.findAnnotatedBeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.tool.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Service
public class LocalRestrictionServiceImpl implements LocalRestrictionService {
    private static final Object[] EMPTY_PARAMETERS = new Object[0];

    private Map<String, List<RestrictionInvocation>> restrictionMethodsByName;

    @Autowired
    private ApplicationContext appContext;

    @PostConstruct
    public void init() {
        Set<Class<?>> restrictionsBeans = findAnnotatedBeans(Restrictions.class);
        restrictionMethodsByName = restrictionsBeans.stream()
            .flatMap(clazz -> {
                    Object bean = appContext.getBean(clazz);
                    return Arrays.stream(clazz.getMethods())
                            .filter( method -> method.isAnnotationPresent(Restriction.class) )
                            .map( method -> new RestrictionInvocation(method.getAnnotation(Restriction.class).value(),
                                                                      bean,
                                                                      method) );
            })
            .collect(Collectors.groupingBy(RestrictionInvocation::getName));
    }

    @Override
    public boolean isAllowed(String name, Object... parameters) {
        parameters = parameters != null ? parameters : EMPTY_PARAMETERS;
        RestrictionInvocation restriction = findRestrictionInvocation(name, parameters);
        if (restriction == null) {
            throw new IllegalArgumentException(String.format("Can't invoke restriction '%s' with values: %s",
                    name,
                    Arrays.stream(parameters).map(String::valueOf).collect(Collectors.joining(","))
            ));
        }

        return restriction.invoke(parameters);
    }

    private RestrictionInvocation findRestrictionInvocation(String name, Object... parameters) {
        List<RestrictionInvocation> restrictions = restrictionMethodsByName.get(name);
        return restrictions == null ? null :
                restrictions.stream()
                        .filter( ri -> ReflectionHelper.checkValueTypesMatch(parameters, ri.getMethod().getParameterTypes()) )
                        .findAny()
                        .orElseGet( parameters.length == 0 ? () -> null :
                                    () -> findRestrictionInvocation(name, EMPTY_PARAMETERS) );
    }

    private class RestrictionInvocation {
        private final String name;
        private final Object bean;
        private final Method method;

        public RestrictionInvocation(String name, Object bean, Method method) {
            this.name = name;
            this.bean = bean;
            this.method = method;
        }

        public Boolean invoke(Object[] params) {
            if (method.getParameterTypes().length == 0) {
                return (Boolean)invokeMethod(method, bean);
            }
            return (Boolean)invokeMethod(method, bean, params);
        }

        public String getName() {
            return name;
        }

        public Object getBean() {
            return bean;
        }

        public Method getMethod() {
            return method;
        }
    }
}
