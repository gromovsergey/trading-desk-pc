package app.programmatic.ui.common.tool.javabean;

import static app.programmatic.ui.common.tool.javabean.PropertyChangeType.ADD;
import static app.programmatic.ui.common.tool.javabean.PropertyChangeType.REMOVE;
import static app.programmatic.ui.common.tool.javabean.PropertyChangeType.UNCHANGED;
import static app.programmatic.ui.common.tool.javabean.PropertyChangeType.UPDATE;
import static app.programmatic.ui.common.tool.javabean.PropertyUtils.isPropertyEmpty;
import static app.programmatic.ui.common.tool.javabean.PropertyUtils.propertiesEqual;

public class PropertyChangeImpl implements PropertyChange {
    private final String name;
    private final Object newValue;
    private final Object prevValue;
    private final boolean isTransferred;

    public PropertyChangeImpl(String name, Object newValue, Object prevValue, boolean isTransferred) {
        this.name = name;
        this.newValue = newValue;
        this.prevValue = prevValue;
        this.isTransferred = isTransferred;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public Object getPrevValue() {
        return prevValue;
    }

    @Override
    public PropertyChangeType getChangeType() {
        if (!isChanged()) {
            return UNCHANGED;
        }

        if (isPropertyEmpty(newValue)) {
            return REMOVE;
        }

        if (isPropertyEmpty(prevValue)) {
            return ADD;
        }

        return UPDATE;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public boolean isChanged() {
        return !isTransferred && !propertiesEqual(newValue, prevValue);
    }
}
