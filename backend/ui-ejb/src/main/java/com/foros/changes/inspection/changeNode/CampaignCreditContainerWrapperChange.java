package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.model.campaign.CampaignCreditContainerWrapper;

public class CampaignCreditContainerWrapperChange extends EntityChange {

    public CampaignCreditContainerWrapperChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
    }

    @Override
    public Long geAuditLogId() {
        return ((CampaignCreditContainerWrapper) object).getId();
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CampaignCreditContainerWrapperChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new CampaignCreditContainerWrapperChange(descriptor, object, changeType, changes);
        }
    }
}