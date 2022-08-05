package com.foros.util;

import com.foros.model.DisplayStatus;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author vladimir
 */
public class BeanUtils {
    private static Logger logger = Logger.getLogger(BeanUtils.class.getName());

    private static final Map<Type, Constructor> factories = new HashMap<Type, Constructor>();

    private BeanUtils() {
    }

    /**
     * Copies bean properties from a specified bean to another bean.<br/>
     *
     * @param dst - Destination object
     * @param src - Source object
     */
    public static void copyProperties(Object dst, Object src, NumberFormat nf) throws Exception {
        copyPropertiesImpl(dst, src, new HashMap<Object, List<Object>>(), nf, false);
    }

    /**
     * Copies bean properties from a specified bean to another bean.<br/>
     *
     * @param dst - Destination object
     * @param src - Source object
     * @param copyNullValues - specified whether setter method will be invoked for properties with null values
     */
    private static void copyProperties(Object dst, Object src, NumberFormat nf, boolean copyNullValues) throws Exception {
        copyPropertiesImpl(dst, src, new HashMap<Object, List<Object>>(), nf, copyNullValues);
    }

    private static void copyPropertiesImpl(Object dst, Object src, Map<Object, List<Object>> createdObjects, NumberFormat nf, boolean copyNullValues) throws Exception {
        if (src == null) {
            throw new IllegalArgumentException("Source bean is null");
        }
        
        if (dst == null) {
            throw new IllegalArgumentException("Destination bean is null");
        }

        List<Object> objs = createdObjects.get(src);
        if (objs == null) {
            objs = new LinkedList<Object>();
            createdObjects.put(src, objs);
        }
        objs.add(dst);

        PropertyDescriptor[] srcInfo;
        PropertyDescriptor[] dstInfo;
        try {
            srcInfo = Introspector.getBeanInfo(src.getClass(), Object.class).getPropertyDescriptors();
            dstInfo = Introspector.getBeanInfo(dst.getClass(), Object.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("Beans introspection failed");
        }

        Map<String, PropertyDescriptor> propertiesToRead = new HashMap<String, PropertyDescriptor>();

        for (PropertyDescriptor prop : srcInfo) {
            propertiesToRead.put(prop.getName(), prop);
        }

        for (PropertyDescriptor prop : dstInfo) {
            if (prop.getWriteMethod() == null) {
                continue;
            }
            
            PropertyDescriptor readDescr = propertiesToRead.get(prop.getName());
            if (readDescr == null) {
                continue;
            }

            Object srcValue = readDescr.getReadMethod().invoke(src);
            if (srcValue == null) {
                if (copyNullValues) {
                    prop.getWriteMethod().invoke(dst, (Object)null);
                }

                continue;
            }

            Type readType = readDescr.getReadMethod().getGenericReturnType();
            Type writeType = prop.getWriteMethod().getGenericParameterTypes()[0];

            Class<?> dstType;
            if (writeType instanceof TypeVariable) {
                // TODO: It's possible to do more precise evaluation of dstType
                dstType = srcValue.getClass();
            } else {
                dstType = prop.getPropertyType();
            }

            Object res;
            try {
                res = getNewPropertyObject(srcValue, dstType, readType, writeType, createdObjects, nf, copyNullValues);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error due invoke " + dst.getClass().getName() + "." + prop.getWriteMethod().getName() + "(value=" + srcValue + ")");
                throw ex;
            }

            // omit calling set method to avoid phantom changes appearance 
            if (!ObjectUtils.equals(res, prop.getReadMethod().invoke(dst))) {
                if (res != null) {
                    prop.getWriteMethod().invoke(dst, res);
                } else {
                    if (srcValue instanceof String && (srcValue.equals("") || ((String)srcValue).trim().equals(""))) {
                        prop.getWriteMethod().invoke(dst, (Object)null);
                    }
                }
            }
        }
        
        // this works only if equals() method of dst returns true if references are equal
        objs.remove(dst); 
        
        if (objs.size() == 0) {
            createdObjects.remove(src);
        }
    }

    private static Object getNewPropertyObject(Object srcValueObject, Class dstType, Type readType, Type writeType, Map<Object, List<Object>> createdObjects, NumberFormat nf, boolean copyNullValues) throws Exception {
        if (srcValueObject instanceof java.lang.Number ||
                srcValueObject instanceof java.lang.Character ||
                srcValueObject instanceof java.lang.Boolean ||
                srcValueObject instanceof java.lang.String ||
                srcValueObject instanceof java.util.Date) {
            return getSimplePropertyObject(srcValueObject, dstType, nf);
        } 
        
        if (srcValueObject instanceof java.util.Collection) {
            return copyCollection((Collection)srcValueObject, dstType, readType, writeType, createdObjects, nf, copyNullValues);
        }

        if (dstType.isEnum() || srcValueObject instanceof DisplayStatus) {
            return srcValueObject;
        }

        if (!dstType.getName().startsWith("java.") && !dstType.getName().startsWith("javax.")) {
            List objSet = createdObjects.get(srcValueObject);
            if (objSet != null) {
                for (Object obj : objSet) {
                    if (obj.getClass().equals(dstType)) {
                        return obj;
                    }
                }
            }
            Object dstInstance = newInstance(dstType);
            copyPropertiesImpl(dstInstance, srcValueObject, createdObjects, nf, copyNullValues);
            return dstInstance;
        }
        
        if (dstType.equals(srcValueObject.getClass())) {
            return srcValueObject;
        }
        
        return null;
    }

    private static Object newInstance(Class dstType) throws Exception {
        Object dstInstance;
        if (!Modifier.isAbstract(dstType.getModifiers())) {
            dstInstance = dstType.newInstance();
        } else {
            dstInstance = newInstanceOfAbstractClass(dstType);
        }
        return dstInstance;
    }

    private synchronized static Object newInstanceOfAbstractClass(Class dstType) throws Exception {
        Constructor factory = factories.get(dstType);
        if (factory == null) {
            // since factories of abstract classes are cached this code be slow (and it is).
            ClassPool cp = new ClassPool(false);
            cp.insertClassPath(new ClassClassPath(dstType));
            CtClass abstractSuperclass = cp.get(dstType.getName());
            CtClass concrete = cp.makeClass(dstType.getName() + "_$$_BEAN_UTIL_$$_");
            concrete.setSuperclass(abstractSuperclass);
            factory = concrete.toClass().getConstructor();
            factories.put(dstType, factory);
        }
        return factory.newInstance();
    }

    private static Object getSimplePropertyObject(Object srcValueObject, Class<?> dstType, NumberFormat nf) throws ParseException {
        if (srcValueObject instanceof java.lang.Number) {
            return copyFromNumber((Number)srcValueObject, dstType, nf);
        }
        
        if (srcValueObject instanceof java.lang.String) {
            return copyFromString((String)srcValueObject, dstType, nf);
        }
        
        if (srcValueObject instanceof java.lang.Character) {
            return copyFromCharacter((Character)srcValueObject, dstType);
        }
        
        if (srcValueObject instanceof java.lang.Boolean) {
            Boolean srcValue = (Boolean)srcValueObject;
            if (java.lang.Boolean.class.equals(dstType)) {
                return srcValue;
            }
            if (java.lang.String.class.equals(dstType)) {
                return srcValue.toString();
            }
            return null;
        }
        
        if (srcValueObject instanceof java.util.Date) {
            if (dstType.isAssignableFrom(srcValueObject.getClass())) {
                return srcValueObject;
            }
            if (java.lang.String.class.equals(dstType)) {
                return srcValueObject.toString();
            }
            if (com.foros.util.UITimestamp.class.equals(dstType)) {
                if (srcValueObject instanceof java.sql.Timestamp) {
                    UITimestamp uit = new UITimestamp();
                    uit.setTime(((java.sql.Timestamp)srcValueObject).getTime());
                    uit.setNanos(((java.sql.Timestamp)srcValueObject).getNanos());
                    return uit;

                } else {
                    return new UITimestamp(((java.util.Date)srcValueObject).getTime());
                }
            }
            throw new IllegalArgumentException("Cannot convert java.util.Date to " + dstType.getName());
        }

        return null;
    }

    private static Object copyFromNumber(Number srcValue, Class dstType, NumberFormat nf) {
        if (java.lang.String.class.equals(dstType)) {
            if (srcValue instanceof BigDecimal) {
                return nf.format(srcValue);
            }
            return srcValue.toString();
        }

        if (java.math.BigDecimal.class.equals(dstType)) {
            if (srcValue instanceof java.math.BigDecimal) {
                return srcValue;
            }
            if (srcValue instanceof java.lang.Float || srcValue instanceof java.lang.Double) {
                return BigDecimal.valueOf(srcValue.doubleValue());
            }
            if (srcValue instanceof java.lang.Byte ||
                     srcValue instanceof java.lang.Short ||
                     srcValue instanceof java.lang.Integer ||
                     srcValue instanceof java.lang.Long) {
                return BigDecimal.valueOf(srcValue.longValue());
            }
            
            return null;
        }
        
        if (java.math.BigInteger.class.equals(dstType)) {
            if (srcValue instanceof java.math.BigInteger) {
                return srcValue;
            }
            
            if (srcValue instanceof java.lang.Byte ||
                     srcValue instanceof java.lang.Short ||
                     srcValue instanceof java.lang.Integer ||
                     srcValue instanceof java.lang.Long ||
                     srcValue instanceof java.lang.Float ||
                     srcValue instanceof java.lang.Double ||
                     srcValue instanceof java.math.BigDecimal) {
                return BigInteger.valueOf(srcValue.longValue());
            }
            
            return null;
        }
        
        if (java.lang.Byte.class.equals(dstType) || byte.class.equals(dstType)) {
            return new Byte(srcValue.byteValue());
        }

        if (java.lang.Double.class.equals(dstType) || double.class.equals(dstType)) {
            return new Double(srcValue.doubleValue());
        }
        
        if (java.lang.Float.class.equals(dstType) || float.class.equals(dstType)) {
            return new Float(srcValue.floatValue());
        }
        
        if (java.lang.Integer.class.equals(dstType) || int.class.equals(dstType)) {
            return new Integer(srcValue.intValue());
        }
        
        if (java.lang.Long.class.equals(dstType) || long.class.equals(dstType)) {
            return new Long(srcValue.longValue());
        }
        
        if (java.lang.Short.class.equals(dstType) || short.class.equals(dstType)) {
             return new Short(srcValue.shortValue());
        }

        return null;
    }

    private static Object copyFromString(String srcValue, Class dstType, NumberFormat nf) throws ParseException {
        if (java.lang.String.class.equals(dstType)) {
            return srcValue;
        }
        
        if (java.lang.Number.class.isAssignableFrom(dstType) ||
                java.lang.Boolean.class.equals(dstType) ||
                java.lang.Character.class.equals(dstType) ||
                byte.class.equals(dstType) ||
                short.class.equals(dstType) ||
                int.class.equals(dstType) ||
                long.class.equals(dstType) ||
                float.class.equals(dstType) ||
                double.class.equals(dstType) ||
                boolean.class.equals(dstType) ||
                char.class.equals(dstType)) {
            if (srcValue != null && !java.lang.Character.class.equals(dstType) && !char.class.equals(dstType)) {
                srcValue = srcValue.trim();
                srcValue = srcValue.replaceAll("[+]", "");
            }
                
            if ("".equals(srcValue) || "".equals(srcValue.trim())) {
                if (byte.class.equals(dstType)) {
                    return (byte)0;
                }
                if (double.class.equals(dstType)) {
                    return (double)0;
                }
                if (float.class.equals(dstType)) {
                    return (float)0;
                }
                if (int.class.equals(dstType)) {
                    return 0;
                }
                if (long.class.equals(dstType)) {
                    return (long)0;
                }
                if (short.class.equals(dstType)) {
                    return (short)0;
                }
                
                return null;
            } 
            
            if (java.math.BigDecimal.class.equals(dstType)) {
                Number res = nf.parse(srcValue);
                if (res instanceof java.lang.Long) {
                    return BigDecimal.valueOf(res.longValue());
                }
                return BigDecimal.valueOf(res.doubleValue());

            }

            if (java.math.BigInteger.class.equals(dstType)) {
                return BigInteger.valueOf(nf.parse(srcValue).longValue());
            }
            
            if (java.lang.Byte.class.equals(dstType) || byte.class.equals(dstType)) {
                return nf.parse(srcValue).byteValue();
            }
            if (java.lang.Double.class.equals(dstType) || double.class.equals(dstType)) {
                return nf.parse(srcValue).doubleValue();
            }
            if (java.lang.Float.class.equals(dstType) || float.class.equals(dstType)) {
                return nf.parse(srcValue).floatValue();
            }
            
            if (java.lang.Integer.class.equals(dstType) || int.class.equals(dstType)) {
                return nf.parse(srcValue).intValue();
            }
            if (java.lang.Long.class.equals(dstType) || long.class.equals(dstType)) {
                return nf.parse(srcValue).longValue();
            }
            if (java.lang.Short.class.equals(dstType) || short.class.equals(dstType)) {
                return nf.parse(srcValue).shortValue();
            }
            if (java.lang.Character.class.equals(dstType) || char.class.equals(dstType)) {
                return Character.valueOf(srcValue.charAt(0));
            }
        }

        return null;
    }

    private static Object copyFromCharacter(Character srcValue, Class dstType) {
        char srcValueChr = srcValue.charValue();
        if (java.lang.Character.class.equals(dstType) || char.class.equals(dstType)) {
            return srcValue;
        }
        
        if (java.lang.String.class.equals(dstType)) {
            return srcValue.toString();
        }

        if (java.lang.Number.class.isAssignableFrom(dstType) && Character.isDigit(srcValueChr)) {
            if (java.math.BigDecimal.class.equals(dstType)) {
                return BigDecimal.valueOf(Long.valueOf(srcValue.toString()));
            }
            
            if (java.math.BigInteger.class.equals(dstType)) {
                return BigInteger.valueOf(Long.valueOf(srcValue.toString()));
            }
            
            if (java.lang.Byte.class.equals(dstType)) {
                return Byte.valueOf(srcValue.toString());
            }
            
            if (java.lang.Double.class.equals(dstType)) {
                return Double.valueOf(srcValue.toString());
            }
            
            if (java.lang.Float.class.equals(dstType)) {
                return Float.valueOf(srcValue.toString());
            }
            
            if (java.lang.Integer.class.equals(dstType)) {
                return Integer.valueOf(srcValue.toString());
            }
            
            if (java.lang.Long.class.equals(dstType)) {
                return Long.valueOf(srcValue.toString());
            }
            
            if (java.lang.Short.class.equals(dstType)) {
                return Short.valueOf(srcValue.toString());
            }
        }
        
        return null;
    }

    private static Collection copyCollection(Collection src, Class dstType, Type readType, Type writeType, Map<Object, List<Object>> createdObjects, NumberFormat nf, boolean copyNullValues) throws Exception {
        Class srcElemType;
        Class dstElemType;
        if (readType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType)readType).getActualTypeArguments();
            if (params.length != 1) {
                throw new IllegalArgumentException(src + " is not a valid collection class");
            }
            srcElemType = (Class)params[0];
        } else {
            throw new IllegalArgumentException(src.getClass() + " is not a valid collection class");
        }

        if (writeType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType)writeType).getActualTypeArguments();
            if (params.length != 1) {
                throw new IllegalArgumentException(dstType + " is not a valid collection class");
            }
            dstElemType = (Class)params[0];
        } else {
            throw new IllegalArgumentException(dstType + " is not a valid collection class");
        }

        Class destRawClass = (Class)((ParameterizedType)writeType).getRawType();
        Collection<Object> res;
        if(destRawClass.isAssignableFrom(java.util.LinkedHashSet.class)){
            res = new LinkedHashSet<Object>();
        }else if(destRawClass.isAssignableFrom(java.util.LinkedList.class)){
            res = new LinkedList<Object>();
        }else{
            throw new IllegalArgumentException("This Collection type is not supported by this Utility");
        }
        
        if (java.lang.Number.class.isAssignableFrom(srcElemType) ||
                java.lang.String.class.isAssignableFrom(srcElemType) ||
                java.lang.Boolean.class.isAssignableFrom(srcElemType) ||
                java.lang.Character.class.isAssignableFrom(srcElemType)) {
            for (Object obj : src) {
                if (obj != null) {
                    res.add(getSimplePropertyObject(obj, dstElemType, nf));
                }
            }
        } else {
            if (!dstElemType.getName().startsWith("java.") &&
                    !dstElemType.getName().startsWith("javax.")) {
                for (Object obj : src) {
                    if (obj != null) {
                        boolean objectAdded = false;
                        
                        List objSet = createdObjects.get(obj);
                        if (objSet != null) {
                            for (Object dstObj : objSet) {
                                if (dstObj.getClass().equals(dstElemType)) {
                                    res.add(dstObj);
                                    objectAdded = true;
                                    break;
                                }
                            }
                        }
                        
                        if (!objectAdded) {
                            Object newObj = dstElemType.newInstance();
                            copyPropertiesImpl(newObj, obj, createdObjects, nf, copyNullValues);
                            res.add(newObj);
                        }
                    }
                }
            }
        }
        
        return res;
    }
}
