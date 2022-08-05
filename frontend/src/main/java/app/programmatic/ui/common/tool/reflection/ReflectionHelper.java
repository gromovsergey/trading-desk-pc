package app.programmatic.ui.common.tool.reflection;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionHelper {
    private static final String SCANNER_BASE_PACKAGE = "app.programmatic.ui";

    public static Set<Class<?>> findAnnotatedBeans(Class<? extends Annotation>... annotationTypes) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        Arrays.asList(annotationTypes).stream()
                .forEach( a -> scanner.addIncludeFilter(new AnnotationTypeFilter(a)) );

        return scanner.findCandidateComponents(SCANNER_BASE_PACKAGE).stream()
                .map( bd -> {
                    try {
                        return Class.forName(bd.getBeanClassName());
                    } catch(ClassNotFoundException e) {
                        // Unreachable code
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    String.format("Can't invoke %s.%s with args: %s",
                            target.getClass(),
                            method.getName(),
                            args == null ? "absent" :
                                    Stream.of(args).map(String::valueOf).collect(Collectors.joining(","))),
                    e);
        }
    }

    public static Method findPublicMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        List<Method> methods = Stream.of(clazz.getMethods())
                .filter( m -> m.getName().equals(name))
                .filter( m -> !m.isSynthetic())
                .filter( m -> returnType.isAssignableFrom(m.getReturnType()))
                .filter( m -> checkParameterTypesAssignableFrom(parameterTypes, m.getParameterTypes()))
                .collect(Collectors.toList());

        if (methods.size() > 1) {
            methods = methods.stream()
                    .filter( m -> checkParameterTypesEqual(parameterTypes, m.getParameterTypes()) )
                    .collect(Collectors.toList());
        }

        if (methods.size() != 1) {
            throwCantFindException(clazz, name, returnType, parameterTypes);
        }

        return methods.iterator().next();
    }

    public static boolean checkValueTypesMatch(Object[] values, Class<?>[] types) {
        if (values.length != types.length) {
            return false;
        }

        Class<?>[] valueTypes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            valueTypes[i] = value != null ? value.getClass() : types[i];
        }

        return checkParameterTypesAssignableFrom(types, valueTypes);
    }

    private static boolean checkParameterTypesAssignableFrom(Class<?>[] list1, Class<?>[] list2) {
        Boolean sizesMatch = checkParameterTypesSizeMatch(list1, list2);
        if (sizesMatch != null) {
            return sizesMatch;
        }

        int i = 0;
        for (Class<?> class1 : list1) {
            if (!class1.isAssignableFrom(list2[i++])) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkParameterTypesEqual(Class<?>[] list1, Class<?>[] list2) {
        Boolean sizesMatch = checkParameterTypesSizeMatch(list1, list2);
        if (sizesMatch != null) {
            return sizesMatch;
        }

        int i = 0;
        for (Class<?> class1 : list1) {
            if (!class1.equals(list2[i++])) {
                return false;
            }
        }

        return true;
    }

    private static Boolean checkParameterTypesSizeMatch(Class<?>[] list1, Class<?>[] list2) {
        int size1 = list1 == null ? 0 : list1.length;
        int size2 = list2 == null ? 0 : list2.length;
        if (size1 != size2) {
            return Boolean.FALSE;
        }
        if (size1 == 0) {
            return Boolean.TRUE;
        }

        return null;
    }

    private static void throwCantFindException(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes) {
        throw new RuntimeException(String.format("Can't find method %s %s.%s(%s)\n" +
                        "Note: please write unit tests to make this code unreachable",
                returnType,
                clazz.getName(),
                name,
                parameterTypes == null ? "" :
                        Stream.of(parameterTypes)
                                .map( c -> c.getName() )
                                .collect(Collectors.joining(", ")))
        );
    }
}