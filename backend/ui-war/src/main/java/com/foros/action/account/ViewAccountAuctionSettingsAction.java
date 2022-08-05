package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.site.TagAuctionSettings;
import com.foros.restriction.annotation.Restrict;

import java.util.List;

public class ViewAccountAuctionSettingsAction extends AccountAuctionSettingsActionBase {

    private List<TagAuctionSettings> tagsAuctionSettings;

    @ReadOnly
    @Restrict(restriction = "AuctionSettings.view")
    public String view() {
        auctionSettings = auctionSettingsService.findByAccountId(getId());
        tagsAuctionSettings = auctionSettingsService.findNonDefaultTags(getId());
        return SUCCESS;
    }

    public List<TagAuctionSettings> getTagsAuctionSettings() {
        return tagsAuctionSettings;
    }
}
