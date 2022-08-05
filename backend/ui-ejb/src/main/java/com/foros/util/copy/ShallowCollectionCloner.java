package com.foros.util.copy;

import com.foros.annotations.CopyPolicy;
import com.foros.util.bean.Filter;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author Vladimir
 */
public class ShallowCollectionCloner implements Cloner {
    public Object clone(Object bean, ClonerContext context) {
        Collection src = (Collection) bean;
        Field srcField = context.getField(bean);

        CopyPolicy copyPolicy = srcField.getAnnotation(CopyPolicy.class);
        Class dstClass = copyPolicy.type();

        if (dstClass == null || !Collection.class.isAssignableFrom(dstClass)) {
            throw new IllegalArgumentException("Type must be specified and must be a collection class");
        }

        try {
            Collection dst = (Collection) dstClass.newInstance();
            Filter<Object> filter = context.getClonerFilter();

            if (filter == null) {
                dst.addAll(src);
            } else {
                for (Object o: src) {
                    if (filter.accept(o)) {
                        dst.add(o);
                    }
                }
            }

            return dst;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create copy of " + bean);
        }
    }
}
