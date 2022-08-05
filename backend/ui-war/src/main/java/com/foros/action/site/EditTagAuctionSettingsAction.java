package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class EditTagAuctionSettingsAction extends TagAuctionSettingsActionBase {

    @ReadOnly
    @Restrict(restriction = "AuctionSettings.update")
    public String edit() {
        auctionSettings = auctionSettingsService.findByTagId(getId());
        return SUCCESS;
    }
}
