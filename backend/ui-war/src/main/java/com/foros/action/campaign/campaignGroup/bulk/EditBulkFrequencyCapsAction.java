package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.framework.ReadOnly;

public class EditBulkFrequencyCapsAction extends CcgEditBulkActionSupport {
    @ReadOnly
    public String edit() {
        return SUCCESS;
    }
}
