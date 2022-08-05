package com.foros.changes.inspection.changeNode.currency;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.changeNode.EntityChange;
import com.foros.changes.inspection.changeNode.EntityChangeUtil;
import com.foros.changes.inspection.changeNode.FieldChange;
import com.foros.model.currency.CurrencyExchangeRateAuditWrapper;

public class CurrencyExchangeRateAuditWrapperChange extends EntityChange {
    public CurrencyExchangeRateAuditWrapperChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
        CurrencyExchangeRateAuditWrapper rateWrapper = (CurrencyExchangeRateAuditWrapper) object;
        EntityChangeUtil.forceFieldChange(descriptor, changes, "updated", rateWrapper.isUpdated());
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CurrencyExchangeRateAuditWrapperChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new CurrencyExchangeRateAuditWrapperChange(descriptor, object, changeType, changes);
        }
    }
}
