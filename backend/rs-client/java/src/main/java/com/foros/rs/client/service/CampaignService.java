package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.campaign.CampaignSelector;

public class CampaignService extends EntityServiceSupport<Campaign, CampaignSelector> {

    public CampaignService(RsClient rsClient) {
        super(rsClient, "/campaigns");
    }
}