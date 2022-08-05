package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkSiteTargetingAction extends BulkSiteTargetingActionSupport {
    @ReadOnly
    public String edit() {
        return SUCCESS;
    }

}
