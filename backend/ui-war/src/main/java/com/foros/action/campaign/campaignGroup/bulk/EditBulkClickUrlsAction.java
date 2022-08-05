package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkClickUrlsAction extends BulkClickUrlsActionSupport {
    @ReadOnly
    public String edit() {
        return SUCCESS;
    }
}
