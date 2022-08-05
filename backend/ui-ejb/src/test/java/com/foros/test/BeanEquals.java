package com.foros.test;

import org.easymock.IArgumentMatcher;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author alexey_koloskov
 */
public class BeanEquals implements IArgumentMatcher {
    private Object expected;
    private String[] props;
    private Class beanClass;

    public BeanEquals(Class beanClass, Object value, String... props) {
        this.beanClass = beanClass;
        this.expected = value;
        this.props = props;
    }

    public boolean matches(Object actual) {
        if (!beanClass.equals(actual.getClass())) {
            return false;
        }

        if (props == null) {
            return CustomAsserts.compareBeans(expected, actual);
        }


        if (expected == null) {
            return actual == null;
        }

        try {
            for (String prop : props) {
                Object actualPropValue = PropertyUtils.getProperty(actual, prop);
                Object expectedPropValue = PropertyUtils.getProperty(expected, prop);

                if (actualPropValue == null) {
                    return expectedPropValue == null;
                }

                if (!actualPropValue.equals(expectedPropValue)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("eqBean(class: " + beanClass + ", properties: {");
        for (int i = 0; props != null && i < props.length; ++i) {
            if (i != 0) {
                buffer.append(", ");
            }
            buffer.append(props[i]);
        }
        buffer.append("}, expected: " + expected + ")");
    }
}
