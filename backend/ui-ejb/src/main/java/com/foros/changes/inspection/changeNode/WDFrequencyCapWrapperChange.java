package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.model.FrequencyCap;
import com.foros.model.admin.WDFrequencyCapWrapper;

import java.util.Map;

public class WDFrequencyCapWrapperChange extends EntityChange {
    public WDFrequencyCapWrapperChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);

        WDFrequencyCapWrapper wdFrequencyCapWrapper = (WDFrequencyCapWrapper) object;
        Map<String, FrequencyCap> elementsToRemove = wdFrequencyCapWrapper.getElementsToRemove();

        for (Map.Entry<String, FrequencyCap> entry : elementsToRemove.entrySet()) {
            forceFieldChange(descriptor, changes, entry.getKey(), entry.getValue());
        }
    }

    private void forceFieldChange(EntityChangeDescriptor descriptor, FieldChange[] changes, String fieldName, Object fieldValue) {
        int i = 0;
        for (FieldChangeDescriptor fieldChangeDescriptor : descriptor.getFieldChangeDescriptors()) {
            if (fieldChangeDescriptor != null && fieldChangeDescriptor.getName().equals(fieldName) && changes.length > i && changes[i] == null) {
                changes[i] = fieldChangeDescriptor.newInstance(fieldValue, null);
            }
            i++;
        }
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected EntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new WDFrequencyCapWrapperChange(descriptor, object, changeType, changes);
        }
    }
}
