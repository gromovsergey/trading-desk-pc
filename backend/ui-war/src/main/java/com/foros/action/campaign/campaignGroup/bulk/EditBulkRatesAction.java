package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkRatesAction extends BulkRatesActionSupport {
    @ReadOnly
    public String edit() {
        return SUCCESS;
    }
}
