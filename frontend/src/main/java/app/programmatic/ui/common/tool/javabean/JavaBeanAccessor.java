package app.programmatic.ui.common.tool.javabean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaBeanAccessor<T> {
    private final Class<T> clazz;
    private final Map<String, MethodHandleProperty> propertyHandles;

    public JavaBeanAccessor(Class<T> clazz, Map<String, MethodHandleProperty> propertyHandles) {
        this.clazz = clazz;
        this.propertyHandles = propertyHandles;
    }

    public <E> E get(T target, String propertyName) {
        try {
            MethodHandleProperty handle = propertyHandles.get(propertyName);
            return (E)handle.getGetMethod().invoke(target);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public <E> void set(T target, String propertyName, E propertyValue) {
        try {
            MethodHandleProperty handle = propertyHandles.get(propertyName);
            handle.getSetMethod().invoke(target, propertyValue);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void moveValueFromTo(T from, T to, String propertyName) {
        moveValueFromTo(from, to, propertyName, true);
    }

    public void moveNotEmptyValueFromTo(T from, T to, String propertyName) {
        moveValueFromTo(from, to, propertyName, false);
    }

    public void moveValueFromTo(T from, T to, String propertyName, TypeCreator creator) {
        moveValueFromTo(from, to, propertyName, creator, true);
    }

    public void moveNotEmptyValueFromTo(T from, T to, String propertyName, TypeCreator creator) {
        moveValueFromTo(from, to, propertyName, creator, false);
    }

    private void moveValueFromTo(T from, T to, String propertyName, TypeCreator creator, boolean copyEmpty) {
        try {
            MethodHandleProperty handle = propertyHandles.get(propertyName);
            Object value = handle.getGetMethod().invoke(from);

            if (!copyEmpty && (value == null || value instanceof Collection && ((Collection)value).isEmpty())) {
                return;
            }

            Object newValue = creator.createWithValueOf(value);
            Object emptyValue = creator.createWithValueOf(null);

            handle.getSetMethod().invoke(to, newValue);
            handle.getSetMethod().invoke(from, emptyValue);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private void moveValueFromTo(T from, T to, String propertyName, boolean copyEmpty) {
        try {
            MethodHandleProperty handle = propertyHandles.get(propertyName);
            Object value = handle.getGetMethod().invoke(from);

            if (value instanceof Collection) {
                moveCollectionValueFromTo(from, to, handle, ((Collection)value), copyEmpty);
                return;
            }

            moveOrdinaryValueFromTo(from, to, handle, value, copyEmpty);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private void moveCollectionValueFromTo(T from, T to, MethodHandleProperty handle, Collection fromValue, boolean copyEmpty) {
        if (!copyEmpty && fromValue.isEmpty()) {
            return;
        }

        try {
            Collection toValue = (Collection)handle.getGetMethod().invoke(to);
            toValue.clear();
            toValue.addAll(fromValue);

            fromValue.clear();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private void moveOrdinaryValueFromTo(T from, T to, MethodHandleProperty handle, Object fromValue, boolean copyEmpty) {
        if (!copyEmpty && fromValue == null) {
            return;
        }

        try {
            handle.getSetMethod().invoke(to, fromValue);
            handle.getSetMethod().invoke(from, null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Set<String> getPropertyNames() {
        return propertyHandles.keySet();
    }

    public void clearProperties(/* ToDo: return type T*/ Object target, Collection<String> propertyNames) {
        propertyNames.stream().forEach(
            propertyName -> clearProperty(target, propertyName)
        );
    }

    public void clearProperty(Object target, String propertyName) {
        try {
            Object targetValue = get((T)target, propertyName);
            if (targetValue instanceof Collection) {
                ((Collection)targetValue).clear();
            } else {
                set((T)target, propertyName, null);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void shallowCopyValuesFromTo(T from, T to) {
        try {
            for (Map.Entry<String, MethodHandleProperty> nameHandle : propertyHandles.entrySet()) {

                MethodHandleProperty handle = nameHandle.getValue();

                Object fromValue = handle.getGetMethod().invoke(from);
                if (fromValue instanceof Collection) {
                    Collection<?> toValue = (Collection<?>)handle.getGetMethod().invoke(to);
                    if (toValue != null) {
                        toValue.clear();
                        toValue.addAll((Collection)fromValue);
                        continue;
                    }
                }

                handle.getSetMethod().invoke(to, fromValue);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Set<String> findDifferencies(T v1, T v2) {
        try {
            Set<String> result = new HashSet(propertyHandles.size());

            for (Map.Entry<String, MethodHandleProperty> nameHandle : propertyHandles.entrySet()) {

                MethodHandleProperty handle = nameHandle.getValue();
                String propertyName = nameHandle.getKey();

                Object value1 = handle.getGetMethod().invoke(v1);
                Object value2 = handle.getGetMethod().invoke(v2);

                if (value1 == null || value2 == null) {
                    if (value1 != value2) {
                        result.add(propertyName);
                    }
                    continue;
                }

                boolean equal = value1 instanceof Collection ? collectionsEqual(((Collection) value1), ((Collection) value2)) :
                        value1.equals(value2);
                if (!equal) {
                    result.add(propertyName);
                }
            }

            return result;

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void walk(JavaBeanWalkCallback<T> callback) {
        String curPropertyName = null;
        try {
            for (Map.Entry<String, MethodHandleProperty> nameHandle : propertyHandles.entrySet()) {
                curPropertyName = nameHandle.getKey();
                callback.process(nameHandle.getKey(), nameHandle.getValue());
            }
        } catch (Throwable t) {
            throw new RuntimeException("Can't process <callback.property>: <" + String.valueOf(callback) + "." + curPropertyName + ">",
                                       t);
        }
    }

    private static boolean collectionsEqual(Collection<Object> col1, Collection<Object> col2) {
        if (col1.size() != col2.size()) {
            return false;
        }

        Iterator<Object> it1 = col1.iterator();
        Iterator<Object> it2 = col2.iterator();
        boolean orderTheSame = true;
        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                orderTheSame = false;
                break;
            }
        }

        if (orderTheSame) {
            return true;
        }

        HashSet<Object> set1 = new HashSet<>(col1);
        HashSet<Object> set2 = new HashSet<>(col2);
        return set1.equals(set2);
    }

    public interface TypeCreator {
        Object createWithValueOf(Object source);

        TypeCreator DEFAULT_PROPERTY_CREATOR = new JavaBeanAccessor.TypeCreator() {
            @Override
            public Object createWithValueOf(Object source) {
                return source == null ? null : source;
            }
        };
        TypeCreator LIST_PROPERTY_CREATOR = new JavaBeanAccessor.TypeCreator() {
            @Override
            public Object createWithValueOf(Object source) {
                if (source == null) {
                    return Collections.emptyList();
                }

                ArrayList result = new ArrayList((List)source);
                return result;
            }
        };
        TypeCreator SET_PROPERTY_CREATOR = new JavaBeanAccessor.TypeCreator() {
            @Override
            public Object createWithValueOf(Object source) {
                if (source == null) {
                    return Collections.emptySet();
                }

                HashSet result = new HashSet((Set)source);
                return result;
            }
        };
    }
}
