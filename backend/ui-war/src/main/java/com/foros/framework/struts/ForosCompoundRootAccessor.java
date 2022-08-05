package com.foros.framework.struts;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

//ToDo: remove after fixing https://issues.apache.org/jira/browse/WW-4350
public class ForosCompoundRootAccessor extends com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor {

    public ForosCompoundRootAccessor() {
        Field invalidMethodsField = null;
        try {
            invalidMethodsField = getClass().getSuperclass().getDeclaredField("invalidMethods");
            invalidMethodsField.setAccessible(true);
            invalidMethodsField.set(this, new ConcurrentHashMap());
        } catch(NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            if (invalidMethodsField != null) {
                invalidMethodsField.setAccessible(false);
            }
        }
    }
}
