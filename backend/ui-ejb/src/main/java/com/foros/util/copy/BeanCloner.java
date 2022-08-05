package com.foros.util.copy;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.util.bean.BeanHelper;
import com.foros.util.bean.Filter;
import com.foros.util.copy.ClonerContext.ClassMetadata;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.proxy.HibernateProxy;

/**
 * Supports bean copy
 * <p/>
 * Idea: to have ability to control copying for object graph by
 * defining suitable strategy for each class property/field
 * i.e. - deep, shallow, against overwritten clone(), etc
 * see: annotation ClonePolicy and its usages, BeanClonerTest
 * <p/>
 * Links
 * http://www.javaworld.com/javaworld/javaqa/2003-01/02-qa-0124-clone.html?page=1
 * http://www.javaworld.com/javaworld/javatips/jw-javatip76.html?page=2
 * http://www.javaworld.com/javaworld/javaqa/2003-12/02-qa-1226-sizeof.html?page=4
 * <p/>
 * Restrictions: works only with JavaBean-like classes without final fields
 */
public abstract class BeanCloner {
    static private Logger logger = Logger.getLogger(BeanCloner.class.getName());

    private static final Set<Class> FINAL_IMMUTABLE_CLASSES; // set in <clinit>
    private static final Set<Class> FINAL_CLONEABLE_CLASSES; // set in <clinit>
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    static {
        FINAL_IMMUTABLE_CLASSES = new HashSet<Class>(17);

        // add some common final/immutable classes:
        FINAL_IMMUTABLE_CLASSES.add(String.class);
        FINAL_IMMUTABLE_CLASSES.add(Byte.class);
        FINAL_IMMUTABLE_CLASSES.add(Short.class);
        FINAL_IMMUTABLE_CLASSES.add(Integer.class);
        FINAL_IMMUTABLE_CLASSES.add(Long.class);
        FINAL_IMMUTABLE_CLASSES.add(Float.class);
        FINAL_IMMUTABLE_CLASSES.add(Double.class);
        FINAL_IMMUTABLE_CLASSES.add(Character.class);
        FINAL_IMMUTABLE_CLASSES.add(Boolean.class);
        FINAL_IMMUTABLE_CLASSES.add(BigDecimal.class);
    }

    static {
        FINAL_CLONEABLE_CLASSES = new HashSet<Class>();

        FINAL_CLONEABLE_CLASSES.add(Timestamp.class);
        FINAL_CLONEABLE_CLASSES.add(Date.class);
        FINAL_CLONEABLE_CLASSES.add(GregorianCalendar.class);
    }

    public static Object clone(final Object bean, Filter<Object> cloneFilter) {
        return clone(bean, new ClonerContext(cloneFilter));
    }

    private BeanCloner() {
    }

    private static Object clone(final Object bean, final ClonerContext context) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Traversing src obj [" + bean + "]");
        }

        if (!checkFilter(bean, context)) {
            return null;
        }

        // return 'obj' clone if it has been instantiated already:
        if (context.containsCloneForObject(bean)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Obj was cloned before [" + bean + "]");
            }
            return context.getClone(bean);
        }

        final Class beanClass = bean.getClass();
        final Object result;

        if (beanClass.isArray()) {
            final int arrayLength = Array.getLength(bean);

            // empty arrays are immutable
            if (arrayLength == 0) {
                context.putClone(bean, bean);
                return bean;
            }

            result = copyArray(bean, context);
            context.putClone(bean, result);
            return result;
        }

        if (bean instanceof Collection) {
            Collection resultCollection = copyCollection((Collection) bean, context);
            context.putClone(bean, resultCollection);
            return resultCollection;
        }

        if (bean instanceof Map) {
            Map resultMap = copyMap((Map) bean, context);
            context.putClone(bean, resultMap);
            return resultMap;
        }

        if (bean instanceof HibernateProxy) {
            Object implementation = ((HibernateProxy)bean).getHibernateLazyInitializer().getImplementation();
            result = reflectivePopulate(implementation, context);
            return result;
        }

        if (isImmutable(beanClass)) {
            context.putClone(bean, bean);
            return bean;
        }

        if (needUseClonable(beanClass)) {
            result = cloneObjectWithStandardClone(bean);
            context.putClone(bean, result);
            return result;
        }

        // fall through to reflectively populating an instance created
        // with a noarg constructor:
        result = reflectivePopulate(bean, context);
        return result;
    }

    private static boolean checkFilter(Object bean, ClonerContext context) {
        Filter<Object> filter = context.getClonerFilter();
        return filter == null || filter.accept(bean);
    }

    /**
     * This method sets clones all declared 'fields' from 'src' to 'dest' and
     * updates the object and metadata maps accordingly.
     *
     * @param src        source object
     * @param dest       src's clone [not fully populated yet]
     * @param fields     fields to be populated
     * @param accessible 'true' if all 'fields' have been made accessible during
     *                   this traversal
     * @param context    internal cloner context
     */
    private static void setFields(final Object src, final Object dest, final Field[] fields, final boolean accessible, final ClonerContext context) {
        for (int f = 0, fieldsLength = fields.length; f < fieldsLength; ++f) {
            final Field field = fields[f];
            final int modifiers = field.getModifiers();

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Dest object [" + dest + "]: field #" + f + ", [" + field + "]");
            }

            if ((Modifier.STATIC & modifiers) != 0) {
                continue;
            }

            if (checkStrategy(field, CopyStrategy.EXCLUDE)) {
                continue;
            }

            // can also skip transient fields here if you want reflective cloning
            // to be more like serialization

            if ((Modifier.FINAL & modifiers) != 0) {
                throw new RuntimeException("Cannot set final field [" + field.getName() + "] of class [" + src.getClass().getName() + "]");
            }

            if (!accessible && ((Modifier.PUBLIC & modifiers) == 0)) {
                try {
                    field.setAccessible(true);
                } catch (SecurityException e) {
                    throw new RuntimeException("Cannot access field [" + field.getName() + "] of class [" + src.getClass().getName() + "]: " + e.toString());
                }
            }

            // to clone and set the field value:
            try {
                Class<? extends Cloner> userClonerClass = getUserCloner(field);
                Object value = BeanHelper.getFieldValue(src, field);

                if (value == null) {
                    field.set(dest, null); // can't assume that the constructor left this as null

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Set field #" + f + ", [" + field + "] of object [" + dest + "]: NULL");
                    }
                } else {
                    final Class valueType = value.getClass();

                    if (checkStrategy(field, CopyStrategy.SETNULL)) {
                        value = null;
                    } else if (!valueType.isPrimitive() && !FINAL_IMMUTABLE_CLASSES.contains(valueType)) {
                        if (!userClonerClass.equals(UndefinedCloner.class)) {  //if user defines his own cloner
                            Cloner userCloner = userClonerClass.newInstance();
                            context.putField(value, field);
                            Object result = userCloner.clone(value, context);
                            context.putClone(value, result);
                            value = result;
                        } else if (checkStrategy(field, CopyStrategy.CLONE)) { //if user specifies to use standard clone() method
                            context.putField(value, field);
                            Object result = cloneObjectWithStandardClone(value);
                            context.putClone(value, result);
                            value = result;
                        } else if (!checkStrategy(field, CopyStrategy.SHALLOW)) { //deep copy (if no annotation is specified cloner uses deep copying)
                            context.putField(value, field);
                            value = clone(value, context);
                        } else if (!isMandatory(field) && !checkFilter(value, context)) {  //shallow copy
                            value = null;
                        }
                    }

                    BeanHelper.setFieldValue(dest, field, value);

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Set field #" + f + ", [" + field + "] of object [" + dest + "]: " + value);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't set field [" + field.getName() + "] of class [" + src.getClass().getName() + "]: " + e.toString(), e);
            }
        }
    }

    private static boolean isImmutable(Class clazz) {
        return FINAL_IMMUTABLE_CLASSES.contains(clazz) || clazz.isEnum();
    }

    private static boolean needUseClonable(Class clazz) {
        return FINAL_CLONEABLE_CLASSES.contains(clazz);
    }

    private static boolean isMandatory(Field field) {
        CopyPolicy copyPolicy = field.getAnnotation(CopyPolicy.class);
        return copyPolicy != null && copyPolicy.mandatory();
    }

    private static boolean checkStrategy(Field field, CopyStrategy strategy) {
        CopyPolicy copyPolicy = field.getAnnotation(CopyPolicy.class);

        return copyPolicy != null && copyPolicy.strategy() == strategy;
    }

    private static Class<? extends Cloner> getUserCloner(Field field) {
        CopyPolicy copyPolicy = field.getAnnotation(CopyPolicy.class);

        if (copyPolicy != null && copyPolicy.strategy().equals(CopyStrategy.CLONE)) {
            return copyPolicy.cloner();
        }

        return UndefinedCloner.class;
    }

    private static Object copyArray(Object obj, ClonerContext context) {
        final Class objClass = obj.getClass();
        final int arrayLength = Array.getLength(obj);
        final Object result;
        final Object newResult;

        final Class componentType = objClass.getComponentType();

        // even though arrays implicitly have a public clone(), it
        // cannot be invoked reflectively, so need to do copy construction:
        result = Array.newInstance(componentType, arrayLength);

        if (componentType.isPrimitive() ||
                FINAL_IMMUTABLE_CLASSES.contains(componentType)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(obj, 0, result, 0, arrayLength);
            return result;
        } else {
            //Counter for new array after skiping null objects
            int newLength = 0;

            for (int i = 0; i < arrayLength; ++i) {
                // recursively clone each array slot:
                final Object slot = Array.get(obj, i);
                if (slot != null) {
                    final Object slotClone = clone(slot, context);
                    if (slotClone != null) {
                        Array.set(result, newLength++, slotClone);
                    }
                }
            }
            //Create new array for new length after skipping null objects
            if (newLength == 0) {
                return null;
            }
            newResult = Array.newInstance(componentType, newLength);
            for (int newCounter = 0; newCounter < newLength; ++newCounter) {
                final Object slot = Array.get(result, newCounter);
                Array.set(newResult, newCounter, slot);
            }

            return newResult;
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection copyCollection(Collection collection, ClonerContext context) {
        Class clazz = getCloneType(collection, context);

        Collection result = (Collection) newInstance(clazz, context.getMetaData(clazz));

        for (Object slot : collection) {
            if (slot != null) {
                final Object slotClone = clone(slot, context);
                if (slotClone != null) {
                    result.add(slotClone);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map copyMap(Map map, ClonerContext context) {
        Class clazz = getCloneType(map, context);
        Map result = (Map) newInstance(clazz, context.getMetaData(clazz));

        Set<Entry> entrySet = map.entrySet();
        for (Map.Entry entrySlot : entrySet) {
            final Object keyClone = clone(entrySlot.getKey(), context);
            final Object valueClone = clone(entrySlot.getValue(), context);

            if (keyClone == null || valueClone == null) {
                continue;
            }
            result.put(keyClone, valueClone);
        }

        return result;
    }

    private static Object cloneObjectWithStandardClone(Object bean) {
        Object result;
        try {
            Method method = bean.getClass().getMethod("clone", EMPTY_CLASS_ARRAY);
            result = method.invoke(bean, EMPTY_OBJECT_ARRAY);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Object " + bean + " cloned [obj.clone()] " + " to " + result);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't call clone() of " + bean + "] of class [" + bean.getClass().getName() + "]", e);
        }
        return result;
    }

    private static Object reflectivePopulate(Object bean, ClonerContext context) {
        Object result;
        Class beanClass = bean.getClass();

        ClassMetadata metadata = context.getMetaData(beanClass);
        result = newInstance(beanClass, metadata);
        context.putClone(bean, result);

        for (Class clazz = beanClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            metadata = context.getMetaData(clazz);

            Field[] declaredFields = metadata.m_declaredFields;
            if (declaredFields == null) {
                declaredFields = clazz.getDeclaredFields();
                metadata.m_declaredFields = declaredFields;
            }

            setFields(bean, result, declaredFields, metadata.m_fieldsAccessible, context);
            metadata.m_fieldsAccessible = true;
        }

        return result;
    }

    private static Object newInstance(Class objClass, ClonerContext.ClassMetadata metadata) {
        Object result;

        // clone = objClass.newInstance () can't handle private constructors
        Constructor noarg = metadata.m_noargConstructor;
        if (noarg == null) {
            try {
                noarg = objClass.getDeclaredConstructor(EMPTY_CLASS_ARRAY);
                metadata.m_noargConstructor = noarg;
            } catch (Exception e) {
                throw new RuntimeException("Class [" + objClass.getName() + "] has no noarg constructor: " + e.toString());
            }
        }

        if (!metadata.m_noargConstructorAccessible &&
                (Modifier.PUBLIC & noarg.getModifiers()) == 0) {
            try {
                noarg.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Can't access noarg constructor [" + noarg + "] of class [" + objClass.getName() + "]: " + e.toString());
            }
            metadata.m_noargConstructorAccessible = true;
        }

        try // to create a clone via the no-arg constructor
        {
            result = noarg.newInstance(EMPTY_OBJECT_ARRAY);
        } catch (Exception e) {
            throw new RuntimeException("Can't instantiate class [" + objClass.getName() + "] using noarg constructor: " + e.toString());
        }

        return result;
    }

    private static Class getCloneType(Object bean, ClonerContext context) {
        Class cloneType = null;
        Field field = context.getField(bean);

        CopyPolicy copyPolicy = field != null ? field.getAnnotation(CopyPolicy.class) : null;
        if (copyPolicy != null) {
            cloneType = copyPolicy.type();
        }

        return (cloneType != null && !cloneType.equals(Void.class)) ? cloneType : bean.getClass();
    }

}
