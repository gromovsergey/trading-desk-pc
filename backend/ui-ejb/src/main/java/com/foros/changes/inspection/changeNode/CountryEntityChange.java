package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.model.Country;

public class CountryEntityChange extends EntityChange {
    public CountryEntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
    }

    @Override
    public Long geAuditLogId() {
        return ((Country)object).getCountryId();
    }

    @Override
    public String getIdProperty() {
        return "code";
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CountryEntityChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new CountryEntityChange(descriptor, object, changeType, changes);
        }
    }
}
