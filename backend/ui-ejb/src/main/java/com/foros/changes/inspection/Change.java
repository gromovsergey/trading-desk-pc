package com.foros.changes.inspection;

/**
 * Information about change of single property.
 */
public final class Change {
    
    private Object newValue;
    private Object oldValue;
    private String propertyName;

    public Change(Object newValue, Object oldValue, String propertyName) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.propertyName = propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public ChangeType getType() {
        if (this.getNewValue() == this.getOldValue()) {
            return ChangeType.UNCHANGED;
        } else if (this.getNewValue() == null && this.getOldValue() != null) {
            return ChangeType.REMOVE;
        } else if (this.getNewValue() != null && this.getOldValue() == null) {
            return ChangeType.ADD;
        } else {
            return ChangeType.UPDATE;
        }
    }

    public Object getLastDefinedValue() {
        return newValue != null ? newValue : oldValue;
    }

    @Override
    public String toString() {
        return "Change[old: " + oldValue + ", new: " + newValue +", property: " + propertyName + "]";
    }

    public void merge(Change change) {
        this.newValue = change.getNewValue();
    }

}
