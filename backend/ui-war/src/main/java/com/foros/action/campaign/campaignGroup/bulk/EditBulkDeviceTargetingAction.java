package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkDeviceTargetingAction extends BulkDeviceTargetingActionSupport {
    @ReadOnly
    public String edit() {
        populateTargeting();
        return SUCCESS;
    }
}
