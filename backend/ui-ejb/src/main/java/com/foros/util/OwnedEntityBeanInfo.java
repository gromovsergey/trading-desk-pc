package com.foros.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * There's workaround for the JRE 1.6 <a href="http://bugs.sun.com/view_bug.do?bug_id=6528714">bug</a>
 */
public abstract class OwnedEntityBeanInfo extends SimpleBeanInfo {
    private static final Logger logger = Logger.getLogger(OwnedEntityBeanInfo.class.getName());

    public static synchronized PropertyDescriptor[] makeGoodDescriptors(Class targetClass) throws Exception {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo(targetClass, Introspector.IGNORE_IMMEDIATE_BEANINFO).getPropertyDescriptors();
        boolean first = true;
        for (int i = 0; i < descriptors.length; i++) {
            PropertyDescriptor descriptor = descriptors[i];
            if (descriptor.getName().equals("account")) {
                descriptors[i] = createGoodDescriptor(targetClass);
                if (first) {
                    first = false;
                } else {
                    logger.log(Level.INFO, "OUI-22337 (forosui): method for account was found again :: class = {0}, method = {1}", new Object[] { targetClass, descriptors[i].getReadMethod() });
                }
            }
        }
        return descriptors;
    }

    private static PropertyDescriptor createGoodDescriptor(Class targetClass) throws Exception {
        // Workaround for http://bugs.sun.com/view_bug.do?bug_id=6788525  ToDo: remove workaround after JDK bugfix
        // Our classes do not have overloaded set / get Account methods, so definition by name will be enough
        Method getMethod = null;
        Method setMethod = null;
        for (Method method: targetClass.getMethods()) {
            if (method.isBridge()) {
                continue;
            }
            if (method.getName().equals("getAccount")) {
                getMethod = method;
            } else if (method.getName().equals("setAccount")) {
                setMethod = method;
            }
            if (getMethod != null && setMethod != null) {
                break;
            }
        }
        //logger.log(Level.INFO, "OUI-22337 (forosui): good descriptor :: class = {0}, method = {1}", new Object[] { targetClass, getMethod, setMethod });
        return new PropertyDescriptor("account", getMethod, setMethod);
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return makeGoodDescriptors(getTaggetClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Class getTaggetClass() {
        String className = getClass().getName();
        String targetName = className.substring(0, className.length() - "BeanInfo".length());
        try {
            return getClass().getClassLoader().loadClass(targetName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
