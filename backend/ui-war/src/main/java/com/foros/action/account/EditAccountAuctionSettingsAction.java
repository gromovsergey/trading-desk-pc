package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class EditAccountAuctionSettingsAction extends EditAccountAuctionSettingsActionBase {

    @ReadOnly
    @Restrict(restriction = "AuctionSettings.update")
    public String edit() {
        auctionSettings = auctionSettingsService.findByAccountId(getId());
        return SUCCESS;
    }

}
