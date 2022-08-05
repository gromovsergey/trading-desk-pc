package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.model.EntityBase;

public class CustomFieldChange extends PrimitiveFieldChange {

    public CustomFieldChange(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        if (isChanged(context)) {
            changeType = ChangeType.UPDATE;
        }
    }

    protected boolean isChanged(PrepareChangesContext context) {
        return ((EntityBase)context.getRoot().getLastDefinedValue()).isChanged(descriptor.getName());
    }

    public static class Factory implements FieldChangeFactory {
        @Override
        public FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            return new CustomFieldChange(descriptor, oldValue, newValue);
        }
    }
}
