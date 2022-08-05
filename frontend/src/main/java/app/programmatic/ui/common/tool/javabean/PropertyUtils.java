package app.programmatic.ui.common.tool.javabean;

import java.util.Collection;
import java.util.Collections;

public class PropertyUtils {

    public static boolean propertiesEqual(Object obj1, Object obj2) {
        if (isPropertyEmpty(obj1)) {
            return isPropertyEmpty(obj2);
        }

        if ((obj1 instanceof Collection)) {
            return collectionsEqual((Collection<?>)obj1,
                    obj2 != null ? (Collection<?>)obj2 : Collections.emptyList());
        }

        return obj1.equals(obj2);
    }

    public static boolean collectionsEqual(Collection<?> col1, Collection<?> col2) {
        return col1.size() == col2.size() && col1.containsAll(col2);
    }

    public static boolean isPropertyEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        }

        return false;
    }
}
