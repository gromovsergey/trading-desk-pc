package com.foros.action.admin.fraudConditions;

import com.foros.framework.ReadOnly;

public class ViewFraudConditionAction extends FraudConditionActionSupport {
    @ReadOnly
    public String view() {
        return doViewEdit();
    }
}
