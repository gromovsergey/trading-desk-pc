package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;

public class EntityChangeUtil {

    public static void forceFieldChange(EntityChangeDescriptor descriptor, FieldChange[] changes, String fieldName, Object fieldValue) {
        int i = 0;
        for (FieldChangeDescriptor fieldChangeDescriptor : descriptor.getFieldChangeDescriptors()) {
            if (fieldChangeDescriptor.getName().equals(fieldName) && changes.length > i && changes[i] == null) {
                changes[i] = fieldChangeDescriptor.newInstance(null, fieldValue);
            }
            i++;
        }
    }
}
