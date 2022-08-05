package app.programmatic.ui.common.tool.javabean.emptyValues;

import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.MethodHandleProperty;
import app.programmatic.ui.common.tool.javabean.PropertyChangeImpl;

/**
 * All empty properties will stay empty
 */
public class DefaultEmptyValuesStrategy implements EmptyValuesStrategy {
    private static DefaultEmptyValuesStrategy instance = new DefaultEmptyValuesStrategy();

    /**
     * All empty properties will stay empty
     */
    @Override
    public <T extends EntityBase> PropertyChangeImpl processProperty(
            String propertyName, MethodHandleProperty handle, T entity, T prev) throws Throwable {
        Object updatedProperty = handle.getGetMethod().invoke(entity);
        Object existingProperty = handle.getGetMethod().invoke(prev);
        return new PropertyChangeImpl(propertyName, updatedProperty, existingProperty, false);
    }

    public static DefaultEmptyValuesStrategy getInstance() {
        return instance;
    }
}
