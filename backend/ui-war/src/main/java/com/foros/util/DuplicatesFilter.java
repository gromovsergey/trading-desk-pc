package com.foros.util;

import com.foros.util.bean.Filter;

import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Returns true for elements whose values retrieved by applying <code>property</code>
 * expression to themselves are equal.
 * <p/>
 * <b>Note:</b> if  <code>removeInaccessible</code> is true, then filtering element is <code>null</code> then it is decided as
 * inaccessible and filter returns false to have this element removed from a collection
 *
 * @author: alexey_chernenko
 */
public class DuplicatesFilter<T> implements Filter<T> {
    private static Logger logger = Logger.getLogger(DuplicatesFilter.class.getName());
    
    private static final Object NOT_ACCESSIBLE_VALUE = new Object();
    private static final Object NULL_VALUE = new Object();

    private Set comparedValues;
    private boolean skipInaccessible;
    private String property;

    public DuplicatesFilter(String property, boolean skipInaccessible) {
        this.comparedValues = new HashSet();
        this.property = property;
        this.skipInaccessible = skipInaccessible;
    }

    public boolean accept(T element) {
        Object fieldValue;

        if (element == null) {
            fieldValue = NULL_VALUE;
        } else {
            fieldValue = getValue(element, property);
        }

        // Nulls are decided as inaccessible, as OGNL expression is not applicable.
        if (fieldValue == NOT_ACCESSIBLE_VALUE || fieldValue == NULL_VALUE) {
            if (!skipInaccessible) {
                return false;
            }
            return true;
        }

        if (!comparedValues.add(fieldValue)) {
           return false;
        }
        return true;
    }

    private static Object getValue(Object source, String property) {
        try {
            return PropertyUtils.getProperty(source, property);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't get a bean property value", e);
            return NOT_ACCESSIBLE_VALUE;
        }
    }

}
