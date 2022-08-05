package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkBidStrategyAction extends BulkBidStrategyActionSupport {
    @ReadOnly
    public String edit() {
        return SUCCESS;
    }
}
