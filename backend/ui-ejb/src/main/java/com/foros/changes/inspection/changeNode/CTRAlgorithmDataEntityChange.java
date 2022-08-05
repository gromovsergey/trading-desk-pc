package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.model.ctra.CTRAlgorithmData;

public class CTRAlgorithmDataEntityChange extends EntityChange {
    public CTRAlgorithmDataEntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
    }

    @Override
    public Long geAuditLogId() {
        return ((CTRAlgorithmData)object).getCountry().getCountryId();
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CTRAlgorithmDataEntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new CTRAlgorithmDataEntityChange(descriptor, object, changeType, changes);
        }
    }
}
