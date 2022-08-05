package app.programmatic.ui.common.tool.javabean.emptyValues;

import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.MethodHandleProperty;
import app.programmatic.ui.common.tool.javabean.PropertyChangeImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * All empty properties other from specified in {@param propertiesToEmpty}
 * will be replaced with previous values (usually from DB)
 * All properties specified in {@param propertiesToEmpty} will receive empty values
 */
public class ConfigurableEmptyValuesStrategy implements EmptyValuesStrategy {
    private Set<String> propertiesToEmpty;

    /**
     * All empty properties other from specified in {@param propertiesToEmpty}
     * will be replaced with previous values (usually from DB)
     * @param propertiesToEmpty properties which will receive empty values
     */
    public ConfigurableEmptyValuesStrategy(Set<String> propertiesToEmpty) {
        this.propertiesToEmpty = Collections.unmodifiableSet(propertiesToEmpty);
    }

    /**
     * All empty properties other from specified in {@param propertiesToEmpty}
     * will be replaced with previous values (usually from DB)
     * All properties specified in {@param propertiesToEmpty} will receive empty values
     */
    @Override
    public <T extends EntityBase> PropertyChangeImpl processProperty(String propertyName,
            MethodHandleProperty handle, T entity, T prev) throws Throwable {
        try {
            Object updatedValue = handle.getGetMethod().invoke(entity);
            Object existingValue = handle.getGetMethod().invoke(prev);

            boolean updatedValueIsEmpty = EmptyValuesStrategy.isValueEmpty(updatedValue);
            boolean intendedToBeEmpty = propertiesToEmpty.contains(propertyName);

            if (intendedToBeEmpty) {
                if (!updatedValueIsEmpty) {
                    clearProperty(handle, entity, updatedValue);
                }
                return new PropertyChangeImpl(propertyName, updatedValue, existingValue, false);
            }

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

    private static final <T extends EntityBase> void clearProperty(
            MethodHandleProperty handle, T target, Object propertyValue) {
        try {
            if (propertyValue instanceof Collection) {
                ((Collection)propertyValue).clear();
            } else {
                handle.getSetMethod().invoke(target, null);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
