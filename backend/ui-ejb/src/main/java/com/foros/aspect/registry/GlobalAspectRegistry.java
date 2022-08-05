package com.foros.aspect.registry;

import com.foros.aspect.AspectInfo;
import com.foros.aspect.registry.find.AspectDescriptors;
import com.foros.aspect.registry.find.AspectFinder;

import com.foros.aspect.registry.find.Aspects;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.springframework.util.ReflectionUtils;

@Startup
@Singleton(name = "AspectRegistry")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GlobalAspectRegistry implements AspectRegistry {

    private static final String CLASS_SEARCH_PATTERN = "classpath*:com/foros/**/*.class";

    private final Logger logger = Logger.getLogger(GlobalAspectRegistry.class.getName());

    @EJB
    private AspectDescriptorFactoryService aspectDescriptorFactoryService;

    private AspectDescriptors aspectDescriptors = new AspectDescriptors();

    @PostConstruct
    public void construct() {
        Aspects aspects = AspectFinder.findAspects(CLASS_SEARCH_PATTERN);

        aspects.iterateFields(new Aspects.AspectListener<Field>() {
            @Override
            public void onValue(Class<?> type, Field field, Set<AspectInfo> infos) {
                for (AspectInfo info : infos) {
                    AspectDescriptor descriptor = aspectDescriptorFactoryService.create(info);
                    PropertyAccessor valueAccessor = new FieldPropertyAccessor(field);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), valueAccessor, descriptor);
                    aspectDescriptors.get(info.getIndex()).property().add(type, field.getName(), propertyDescriptor);

                    logger.fine("Aspect " + info + " registered for property " + type.getName() + "." + field.getName());
                }
            }
        });

        aspects.iterateMethods(new Aspects.AspectListener<Method>() {
            @Override
            public void onValue(Class<?> type, Method method, Set<AspectInfo> infos) {
                for (AspectInfo info : infos) {
                    AspectDescriptor descriptor = aspectDescriptorFactoryService.create(info);
                    if (info.forProperty()) {
                        PropertyAccessor propertyAccessor = new MethodPropertyAccessor(method);
                        String propertyName = fetchPropertyName(method.getName());
                        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyAccessor, descriptor);
                        aspectDescriptors.get(info.getIndex()).property().add(type, propertyName, propertyDescriptor);

                        logger.fine("Aspect " + info + " registered for property " + type.getName() + "." + propertyName);
                    } else {
                        MethodDescriptor methodDescriptor = new MethodDescriptor(method, descriptor);
                        aspectDescriptors.get(info.getIndex()).method().add(type, method, methodDescriptor);

                        logger.fine("Aspect " + info + " registered for method: " + method);
                    }
                }
            }

            private String fetchPropertyName(String name) {
                int prefixLength = name.startsWith("is") ? 2 : 3;
                String property = name.substring(prefixLength);
                String firstLetter = String.valueOf(property.charAt(0));
                return firstLetter.toLowerCase() + property.substring(1);
            }

        });
    }


    private static class FieldPropertyAccessor implements PropertyAccessor {
        private final Field field;

        private FieldPropertyAccessor(final Field field) {
            this.field = field;
        }

        @Override
        public Object getValue(Object bean) {
            synchronized(field) {
                boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    return ReflectionUtils.getField(field, bean);
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }
    }

    private static class MethodPropertyAccessor implements PropertyAccessor {
        private final Method method;

        private MethodPropertyAccessor(final Method method) {
            this.method = method;
        }

        @Override
        public Object getValue(Object bean) {
            synchronized(method) {
                boolean accessible = method.isAccessible();
                try {
                    method.setAccessible(true);
                    return ReflectionUtils.invokeMethod(method, bean);
                } finally {
                    method.setAccessible(accessible);
                }
            }
        }
    }

    @Override
    public AspectDescriptor getDescriptor(Class<? extends Annotation> index, Method method) {
        Set<MethodDescriptor> methodDescriptors = aspectDescriptors.get(index).method().get(method.getDeclaringClass(), method);

        if (methodDescriptors == null || methodDescriptors.isEmpty()) {
            return null;
        }

        return methodDescriptors.iterator().next().getAspectDescriptor(); // todo!!!
    }

    @Override
    public Map<String, Set<PropertyDescriptor>> getPropertyDescriptors(Class<? extends Annotation> index, Class<?> beanType) {
        return aspectDescriptors.get(index).property().get(beanType);
    }

}
