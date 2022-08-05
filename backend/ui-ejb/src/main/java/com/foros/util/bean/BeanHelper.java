package com.foros.util.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class BeanHelper {

    private static Map<Class, BeanInfo> beanInfoCache = Collections.synchronizedMap(new WeakHashMap<Class, BeanInfo>());

    private BeanHelper() {
    }

    /**
     * Returns the value of the given field (may be private) in the given object
     *
     * @param object The object containing the field, null for static fields
     * @param field  The field, not null
     * @return The value of the given field in the given object
     * @throws RuntimeException if the field could not be accessed
     */
    public static Object getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error while trying to access field " + field, e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while trying to access field " + field, e);
        }
    }

    //todo:  controvertial name, need to rename both this and findFieldValue() methods 
    public static Object getFieldValue(Object bean, String fieldName) {
        boolean isRead = false;
        Object result = null;

        PropertyDescriptor[] propertyDescriptors;
        try {
            propertyDescriptors = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
            for (PropertyDescriptor prop : propertyDescriptors) {
                if (prop.getName().equals(fieldName)) {
                    result = prop.getReadMethod().invoke(bean);
                    isRead = true;
                }
            }

            if (!isRead) {
                Field field = bean.getClass().getField(fieldName);
                field.setAccessible(true);
                result = field.get(bean);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot get field [" + fieldName +
                    "] of class [" + bean.getClass().getName() + "]: ", e);
        }

        return result;
    }

    /**
     * @param array
     * @param path  e.g. - "0.fieldName" or "0.fieldName1,2.fieldName2"
     * @return object for single field and List for multiple-field-path
     */
    public static Object getFieldValue(Object[] array, String path) {
        Object result;
        try {
            if (path.indexOf(",") == -1) {
                result = getSingleField(array, path);
            } else {
                List<Object> listResult = new LinkedList<Object>();
                String[] pathElements = path.split("[,]");
                for (String pathElement : pathElements) {
                    listResult.add(getSingleField(array, pathElement));
                }
                result = listResult;
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException("Wrong path : " + path + ", cause: " + e);
        }

        return result;
    }

    private static Object getSingleField(Object[] array, String pathElement) {
        Object result;
        if (pathElement.indexOf(".") == -1) {
            result = array[Integer.parseInt(pathElement)];
        } else {
            String[] fields = pathElement.split("[.]");
            result = array[Integer.parseInt(fields[0])];
            for (int i = 1; i < fields.length; i++) {
                result = BeanHelper.getFieldValue(result, fields[i]);
            }
        }

        return result;
    }

    /**
     * Sets the given value to the given field on the given object
     *
     * @param object The object containing the field, not null
     * @param field  The field, not null
     * @param value  The value for the given field in the given object
     * @throws RuntimeException if the field could not be accessed
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to assign the value to field: " + field.getName() +
                    ". Ensure that this field is of the correct type.", e);

        } catch (IllegalAccessException e) {
            // Cannot occur, since field.accessible has been set to true
            throw new RuntimeException("Error while trying to access field " + field, e);
        }
    }

    /**
     * Creates an instance of the given type
     *
     * @param <T>  The type of the instance
     * @param type The type of the instance
     * @return An instance of this type
     * @throws RuntimeException If an instance could not be created
     */
    public static <T> T newInstance(Class<T> type) {
        return newInstance(type, false);
    }

    /**
     * Creates an instance of the given type
     *
     * @param <T>                 The type of the instance
     * @param type                The type of the instance
     * @param bypassAccessibility If true, no exception is thrown if the parameterless constructor is not public
     * @return An instance of this type
     * @throws RuntimeException If an instance could not be created
     */
    public static <T> T newInstance(Class<T> type, boolean bypassAccessibility) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (bypassAccessibility) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();

        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create object of class " + type.getName(), e);
        }
    }

    /**
     * Finds field by name.
     *
     * @param object
     * @param fieldName
     * @return null if nothing has been found
     */
    public static Field findField(Object object, String fieldName) {
        return findField(object.getClass(), fieldName);
    }
    
    public static Field findField(Class clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.getName().equals(fieldName)) {
                    return declaredField;
                }
            }
        }
        return null;
    }

    /**
     * Returns value of field, despite field's modifier.
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object findFieldValue(Object object, String fieldName) {
        Field field = BeanHelper.findField(object, fieldName);
        if (field == null) {
            throw new RuntimeException("cannot find member field - " + fieldName);
        }
        return BeanHelper.getFieldValue(object, field);
    }

}
