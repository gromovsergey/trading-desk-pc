package com.foros.aspect.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class LookupUtil {

    private LookupUtil() {
    }

    public static <T> T lookup(Class<?> type) {
        try {
            InitialContext initialContext = new InitialContext();
            String beanName = type.getSimpleName();
            String jndiLink = "java:app/foros-ui-ejb/" + beanName;
            return (T) initialContext.lookup(jndiLink);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
