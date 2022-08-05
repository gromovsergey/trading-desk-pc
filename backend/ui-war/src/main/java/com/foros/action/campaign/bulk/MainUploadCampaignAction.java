package com.foros.action.campaign.bulk;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class MainUploadCampaignAction extends UploadCampaignActionSupport {

    @ReadOnly
    @Restrict(restriction = "BulkTextCampaignUpload.upload", parameters = "find('AdvertiserAccount', #target.advertiserId)")
    public String main() {
        return SUCCESS;
    }
}
