package com.foros.test;

import org.easymock.EasyMock;

/**
 * @author alexey_koloskov
 */
public class CustomMatchers {
    public static <T> T eqBean(Class<T> beanClass, Object value, String... props) {
        EasyMock.reportMatcher(new BeanEquals(beanClass, value, props));
        return null;
    }
}
