package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.model.creative.CreativeCategory;

public class CreativeCategoryEntityChange extends EntityChange {

    public CreativeCategoryEntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);

        CreativeCategory creativeCategory = (CreativeCategory) object;

        boolean hasChangesReally = false;
        for (FieldChange fieldChange : changes) {
            if (fieldChange != null) {
                hasChangesReally = true;
                break;
            }
        }

        if (hasChangesReally) {
            forceFieldChange(descriptor, changes, "defaultName", creativeCategory.getDefaultName());
            forceFieldChange(descriptor, changes, "qaStatus", creativeCategory.getQaStatus());
            forceFieldChange(descriptor, changes, "type", creativeCategory.getType());
        }
    }

    private void forceFieldChange(EntityChangeDescriptor descriptor, FieldChange[] changes, String fieldName, Object fieldValue) {
        int i = 0;
        for (FieldChangeDescriptor fieldChangeDescriptor : descriptor.getFieldChangeDescriptors()) {
            if (fieldChangeDescriptor.getName().equals(fieldName) && changes.length > i &&  changes[i] == null) {
                changes[i] = fieldChangeDescriptor.newInstance(null, fieldValue);
            }
            i++;
        }
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CreativeCategoryEntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new CreativeCategoryEntityChange(descriptor, object, changeType, changes);
        }
    }
}
