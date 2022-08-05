package com.foros.test;

import org.apache.commons.beanutils.PropertyUtils;
import static java.beans.Introspector.*;
import java.beans.PropertyDescriptor;
import java.util.Set;
import java.util.HashSet;
import org.junit.Assert;

public class CustomAsserts {
    private static Set<String> SKIP_EQUALS = new HashSet<String>() {
        {
            add("allowedStatuses");
            add("changes");
            add("inheritedStatus");
            add("parentStatus");
        }
    };
    
    public static boolean compareBeans(Object expected, Object actual) {
        try {
            if (expected == null && actual == null) {
                return true;
            }
            
            if (actual != expected && (actual == null || expected == null)) {
                return false;
            }
            
            if (!expected.getClass().equals(actual.getClass())) {
                return false;
            }

            PropertyDescriptor[] props = getBeanInfo(expected.getClass(),
                    expected.getClass().getSuperclass()).getPropertyDescriptors();

            for (PropertyDescriptor desc : props) {
                Object expectedValue = PropertyUtils.getProperty(expected, desc.getName());
                Object actualValue = PropertyUtils.getProperty(actual, desc.getName());

                if (actualValue != expectedValue &&
                        (actualValue == null || expectedValue == null || !expectedValue.equals(actualValue))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }

    public static void assertEqualsBean(String msg, Object expected, Object actual) {
        if (msg == null) {
            msg = "Beans don't equal";
        }
        
        boolean isEqual = true;

        if (expected == null && actual == null) {
            return;
        }
        
        if (actual != expected && (actual == null || expected == null)) {
            Assert.fail(msg + ", one of the beans is null");
        }

        if (!expected.getClass().equals(actual.getClass())) {
            Assert.fail(msg + 
                    ", different bean classes: expected=" + expected.getClass() +
                    ", actual=" + expected.getClass());
        }

        try {
            PropertyDescriptor[] props = getBeanInfo(expected.getClass(),
                    expected.getClass().getSuperclass()).getPropertyDescriptors();

            for (PropertyDescriptor desc : props) {
                if(SKIP_EQUALS.contains(desc.getName())) {
                    continue;
                }

                Object expectedValue = PropertyUtils.getProperty(expected, desc.getName());
                Object actualValue = PropertyUtils.getProperty(actual, desc.getName());

                if (actualValue != expectedValue &&
                        (actualValue == null || expectedValue == null || !expectedValue.equals(actualValue))) {
                    msg += ", property '" + desc.getName() + 
                           "': expected=" + expectedValue +
                           ", actual=" + actualValue;
                    isEqual = false;
                }
            }

        } catch (Exception e) {
            Assert.fail(msg + ", exception: " + e.getMessage());
        }

        if (!isEqual) {
            Assert.fail(msg);
        }
    }

    public static void assertEqualsBean(Object expected, Object actual) {
        assertEqualsBean(null, expected, actual);
    }
}
