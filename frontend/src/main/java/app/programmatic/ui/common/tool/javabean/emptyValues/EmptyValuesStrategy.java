package app.programmatic.ui.common.tool.javabean.emptyValues;

import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.MethodHandleProperty;
import app.programmatic.ui.common.tool.javabean.PropertyChangeImpl;

import java.util.Collection;

public interface EmptyValuesStrategy {
    <T extends EntityBase> PropertyChangeImpl processProperty(
            String propertyName, MethodHandleProperty handle, T entity, T prev) throws Throwable;

    static boolean isValueEmpty(Object value) {
        return value == null ||
                value instanceof Collection && ((Collection) value).isEmpty();
    }
}
