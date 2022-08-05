package com.foros.changes.inspection.changeNode.currency;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.changeNode.EntityChange;
import com.foros.changes.inspection.changeNode.EntityChangeUtil;
import com.foros.changes.inspection.changeNode.FieldChange;
import com.foros.model.currency.CurrencyExchangeAuditWrapper;

public class CurrencyExchangeAuditWrapperChange extends EntityChange {

    public CurrencyExchangeAuditWrapperChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
        EntityChangeUtil.forceFieldChange(descriptor, changes, "source", ((CurrencyExchangeAuditWrapper) object).getSource());
    }

    @Override
    public Long geAuditLogId() {
        return ((CurrencyExchangeAuditWrapper) object).getExchange().getId();
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected CurrencyExchangeAuditWrapperChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
                return new CurrencyExchangeAuditWrapperChange(descriptor, object, changeType, changes);
        }
    }
}
