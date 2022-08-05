package com.foros.session.campaign.bulk;

import com.foros.model.campaign.Campaign;
import com.foros.model.creative.Creative;

import java.io.Serializable;
import java.util.List;

public final class BulkParseResult implements Serializable {

    private final List<Campaign> campaigns;
    private final List<Creative> creatives;

    public BulkParseResult(List<Campaign> campaigns, List<Creative> creatives) {
        this.campaigns = campaigns;
        this.creatives = creatives;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public List<Creative> getCreatives() {
        return creatives;
    }
}