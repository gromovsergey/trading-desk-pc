package com.foros.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author alexey_chernenko
 */
public class EqualsUtil {

    /**
     * Compare two string. Next values are equal:
     * 1) Null and "" and "   "
     * 2) " xxx  " and "xxx"
     */
    public static boolean stringEquals(String source, String dest) {
        if (ObjectUtils.equals(source, dest)) {
            return true;
        }
        
        if (StringUtil.isPropertyEmpty(source)) {
            if (StringUtil.isPropertyEmpty(dest)) {
                return true;
            }
            return false;
        }
        
        if (StringUtil.isPropertyEmpty(dest)) {
            return false;
        }
                
        return source.trim().equals(dest.trim());
    }

    /**
     * Applys compareTo(Object)  method to identify equality, rather then equals(Object)
     */
    public static <T extends Comparable> boolean equalsComparable(T source, T dest) {
        return source == dest || source != null && dest != null && source.compareTo(dest) == 0;
    }

    public static boolean equalsBigDecimal(BigDecimal source, BigDecimal dest) {
        return equalsBigDecimal(source, dest, 5);
    }

    private static boolean equalsBigDecimal(BigDecimal source, BigDecimal dest, int scale) {
        return source == dest || source != null && dest != null && 
                source.setScale(scale, RoundingMode.HALF_EVEN).compareTo(dest.setScale(scale, RoundingMode.HALF_EVEN)) == 0;
    }

    public static boolean equals(Object... objects) {
        if (objects.length % 2 != 0) {
            return false;
        }

        for (int i = 0; i < objects.length / 2; ++i) {
            if (!ObjectUtils.equals(objects[2*i], objects[2*i + 1])) {
                return false;
            }
        }

        return true;
    }

}
