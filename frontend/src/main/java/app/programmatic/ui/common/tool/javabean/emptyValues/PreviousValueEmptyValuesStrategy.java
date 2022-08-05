package app.programmatic.ui.common.tool.javabean.emptyValues;

import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.MethodHandleProperty;
import app.programmatic.ui.common.tool.javabean.PropertyChangeImpl;

import java.util.Collection;

/**
 * All empty properties will be replaced with previous values (usually from DB)
 */
public class PreviousValueEmptyValuesStrategy implements EmptyValuesStrategy {
    private static PreviousValueEmptyValuesStrategy instance = new PreviousValueEmptyValuesStrategy();

    /**
     * All empty properties will be replaced with previous values (usually from DB)
     */
    @Override
    public <T extends EntityBase> PropertyChangeImpl processProperty(
            String propertyName, MethodHandleProperty handle, T entity, T prev) throws Throwable {
        try {
            Object updatedValue = handle.getGetMethod().invoke(entity);
            Object existingValue = handle.getGetMethod().invoke(prev);

            boolean updatedValueIsEmpty = EmptyValuesStrategy.isValueEmpty(updatedValue);

            if (!updatedValueIsEmpty ||
                    updatedValueIsEmpty && EmptyValuesStrategy.isValueEmpty(existingValue)) {
                return new PropertyChangeImpl(propertyName, updatedValue, existingValue, false);
            }

            // ToDo: Please remove workaround (for two persistent collections) after Issue #529
            if (updatedValue instanceof Collection) {
                Collection<?> updatedCollectionValue = (Collection)updatedValue;
                updatedCollectionValue.clear();
                updatedCollectionValue.addAll((Collection)existingValue);
            } else {
                handle.getSetMethod().invoke(entity, existingValue);
            }
            return new PropertyChangeImpl(propertyName, existingValue, existingValue, true);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static PreviousValueEmptyValuesStrategy getInstance() {
        return instance;
    }
}
